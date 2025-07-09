package com.playblog.userservice.auth.service;

import com.playblog.userservice.auth.dto.LoginRequestDto;
import com.playblog.userservice.auth.dto.TokenResponseDto;
import com.playblog.userservice.auth.entity.RefreshToken;
import com.playblog.userservice.auth.jwt.JwtTokenProvider;
import com.playblog.userservice.auth.repository.RefreshTokenRepository;
import com.playblog.userservice.user.Role;
import com.playblog.userservice.user.User;
import com.playblog.userservice.user.UserRepository;
import com.playblog.userservice.user.UserService;
import com.playblog.userservice.user.dto.UserRegisterRequestDto;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class AuthServiceTest {

  @Autowired
  private UserService userService;

  private static final Logger log = LoggerFactory.getLogger(AuthServiceTest.class);

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private AuthService authService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Captor
  private ArgumentCaptor<RefreshToken> tokenCaptor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // 실제 JwtTokenProvider 생성 (비밀키와 만료시간은 테스트용 값 입력)
    jwtTokenProvider = new JwtTokenProvider();
    ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret",
        "LH9QZL8upsPBfuDY+Dkb1kT9DZIIUSuA2u4O6Lfi3mkEfeWtETpVTcR/8SMZdJWn/xNTuCQBE6rBvDXgnVmscQ==");  // 테스트용 비밀키
    ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpiration", 3600000L);    // 1시간
    ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpiration", 86400000L);  // 24시간

    jwtTokenProvider.init();

    // authService에 실제 JwtTokenProvider 주입 (기존 mock 대신)
    ReflectionTestUtils.setField(authService, "jwtTokenProvider", jwtTokenProvider);
  }

  @Test
  void 로그인_시_액세스_토큰과_리프레시_토큰을_발급하여_저장한다() {
    String emailId = "login_test";
    String password = "rawPass";
    String deviceId = "device123";

    // 1. 해당 사용사자 없으면 회원가입한다.
    if (!userRepository.existsByEmailId(emailId)) {
      userService.registerUser(new UserRegisterRequestDto(emailId, password));
    }

    // 2. 로그인 호출
    LoginRequestDto loginDto = new LoginRequestDto(emailId, password, deviceId);
    TokenResponseDto token = authService.login(loginDto);

    // 3. 검증
    assertThat(token.getAccessToken()).isNotBlank();
    assertThat(token.getRefreshToken()).isNotBlank();
    assertThat(refreshTokenRepository.findByToken(token.getAccessToken())).isNotNull();
    log.info("refresh token: {}", token.getRefreshToken());
    log.info("access token: {}", token.getAccessToken());
  }


  @Test
  void 리프레시토큰과_액세스토큰을_재발급한다() throws InterruptedException {
    // given
    String emailId = "refresh_test";
    String password = "rawPass";
    String deviceId = "device123";

    // 1. 해당 사용자가 없으면 회원가입한다.
    if (!userRepository.existsByEmailId(emailId)) {
      userService.registerUser(new UserRegisterRequestDto(emailId, password));
    }

    // 2. 로그인하여 토큰을 발급한다.
    TokenResponseDto tokenResponseDto = authService.login(
        new LoginRequestDto(emailId, password, deviceId));
    String oldRefreshToken = tokenResponseDto.getRefreshToken();
    String oldAccessToken = tokenResponseDto.getAccessToken();

    // wait 3초 (토큰 재발급 시각 차이 확인용)
    Thread.sleep(3000);

    // when: reissueAccessToken 호출하여 토큰을 재발급한다.
    TokenResponseDto response = authService.reissueAccessToken(oldRefreshToken, deviceId);

    log.info("Old AccessToken     : {}", oldAccessToken);
    log.info("New AccessToken     : {}", response.getAccessToken());
    log.info("Old RefreshToken    : {}", oldRefreshToken);
    log.info("New RefreshToken    : {}", response.getRefreshToken());

    // then: 새 토큰이 생성되고, 저장소에 반영되었는지 확인
    assertThat(response.getAccessToken())
        .as("AccessToken이 비어있지 않아야 함")
        .isNotBlank();

    assertThat(response.getRefreshToken())
        .as("RefreshToken이 비어있지 않아야 함")
        .isNotBlank();

    assertThat(response.getAccessToken())
        .as("AccessToken이 이전 것과 달라야 함")
        .isNotEqualTo(oldAccessToken);

    assertThat(response.getRefreshToken())
        .as("RefreshToken이 이전 것과 달라야 함")
        .isNotEqualTo(oldRefreshToken);



    // DB에 저장된 refreshToken 값이 새로 발급된 값과 같은지 확인
    RefreshToken updatedToken = refreshTokenRepository.findByEmailIdAndDeviceId(emailId, deviceId)
        .orElseThrow(() -> new AssertionError("리프레시 토큰이 저장되지 않았습니다."));

    assertThat(updatedToken.getToken())
        .as("DB에 저장된 RefreshToken이 새로 발급된 값과 일치해야 함")
        .isEqualTo(response.getRefreshToken());
  }

  @Test
  void 디바이스별_새로운_리프레시_토큰을_발급하고_저장한다() {
    // given
    String emailId = "diffrent_device_test";
    String password = "encodedPass";
    String role = "USER";
    Long userId = 1L;
    String deviceId1 = "deviceA";
    String deviceId2 = "deviceB";

    User user = User.builder()
        .id(userId)
        .emailId(emailId)
        .password(password)
        .role(Role.USER)
        .build();

    // 1. 테스트용 회원이 없다면 회원가입한다
    if (!userRepository.existsByEmailId(emailId)) {
      userService.registerUser(new UserRegisterRequestDto(emailId, password));
    }

    // when
    LoginRequestDto request1 = new LoginRequestDto(emailId, password, deviceId1);
    LoginRequestDto request2 = new LoginRequestDto(emailId, password, deviceId2);

    // 2. 같은 회원이 다른 기기에서 로그인한다.
    TokenResponseDto tokenResponseDto1 = authService.login(request1);
    TokenResponseDto tokenResponseDto2 = authService.login(request2);

    // then
    assertThat(tokenResponseDto1.getRefreshToken())
        .as("device1 로그인 시, 토큰 값이 비어있지 않아야 함.").isNotBlank();

    assertThat(tokenResponseDto2.getRefreshToken())
        .as("device2 로그인 시, 토큰 값이 비어있지 않아야 함.").isNotBlank();

    assertThat(tokenResponseDto1.getRefreshToken())
        .as("device1과 device2의 리프레시 토큰값이 같지 않아야 함.").isNotEqualTo(
        tokenResponseDto2.getRefreshToken());

    // DB에서 각각 deviceId별로 RefreshToken 저장 확인
    RefreshToken savedToken1 = refreshTokenRepository.findByEmailIdAndDeviceId(emailId, deviceId1)
        .orElseThrow(() -> new AssertionError("deviceA의 리프레시 토큰이 저장되지 않았습니다."));
    RefreshToken savedToken2 = refreshTokenRepository.findByEmailIdAndDeviceId(emailId, deviceId2)
        .orElseThrow(() -> new AssertionError("deviceB의 리프레시 토큰이 저장되지 않았습니다."));

    assertThat(savedToken1.getToken())
        .as("device1의 토큰값이 db에 저장된 값과 일치해야 함").isEqualTo(tokenResponseDto1.getRefreshToken());

    assertThat(savedToken2.getToken())
        .as("device2의 토큰값이 db에 저장된 값과 일치해야 함").isEqualTo(tokenResponseDto2.getRefreshToken());

    assertThat(savedToken1.getToken())
        .as("db에 저장된 device1과 device2의 토큰값이 같지 않아야 함").isNotEqualTo(savedToken2.getToken());

  }


  @Test
  void 로그아웃시_저장된_리프레시토큰을_지운다() {
    // given
    String emailId = "login_test";
    String password = "rawPass";
    String deviceId = "device123";

    // 1. 해당 사용사자 없으면 회원가입한다.
    if (!userRepository.existsByEmailId(emailId)) {
      userService.registerUser(new UserRegisterRequestDto(emailId, password));
    }

    // 2. 로그인 호출
    LoginRequestDto loginDto = new LoginRequestDto(emailId, password, deviceId);
    TokenResponseDto token = authService.login(loginDto);
    // DB에서 해당 refreshToken이 저장됐는지
    boolean exists = refreshTokenRepository.findByEmailIdAndDeviceId(emailId, deviceId).isPresent();
    assertThat(exists).isTrue();  // 삭제되어야 하므로 false여야 함

    // when
    authService.logout(token.getRefreshToken(), deviceId);

    // then: DB에서 해당 refreshToken이 삭제됐는지 확인
    exists = refreshTokenRepository.findByEmailIdAndDeviceId(emailId, deviceId).isPresent();
    assertThat(exists).isFalse();  // 삭제되어야 하므로 false여야 함
  }

/*  @Test
  void reissuAccessToken_invalidToken_throwsException() {
    log.info("[refreshToken_invalidToken_throwsException] 테스트 시작");
    when(jwtTokenProvider.validateToken("invalid-token")).thenReturn(false);

    assertThatThrownBy(() -> authService.reissueAccessToken("invalid-token", "device123"))
        .isInstanceOf(BadCredentialsException.class)
        .hasMessageContaining("Invalid Refresh Token");

    log.info("[refreshToken_invalidToken_throwsException] 테스트 종료");
  }




  @Test
  void logout_invalidToken_throwsException() {
    log.info("[logout_invalidToken_throwsException] 테스트 시작");
    when(jwtTokenProvider.validateToken("invalid-token")).thenReturn(false);

    assertThatThrownBy(() -> authService.logout("invalid-token", "device123"))
        .isInstanceOf(BadCredentialsException.class)
        .hasMessageContaining("Invalid Refresh Token");

    log.info("[logout_invalidToken_throwsException] 테스트 종료");
  }*/
}


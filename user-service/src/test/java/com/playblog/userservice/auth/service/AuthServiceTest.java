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
  private UserService userService ;

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

    // 2. 로그인 호출
    LoginRequestDto loginDto = new LoginRequestDto("test1", "rawPass", "device123");
    TokenResponseDto token = authService.login(loginDto);


    // 3. 검증
    assertThat(token.getAccessToken()).isNotBlank();
    assertThat(token.getRefreshToken()).isNotBlank();
    assertThat(refreshTokenRepository.findByToken(token.getAccessToken())).isNotNull();
    log.info("refresh token: {}", token.getRefreshToken());
    log.info("access token: {}", token.getAccessToken());
  }




  /*@Test
  @DisplayName("리프레시 토큰과 액세스 토큰 재발급 테스트")
  void refreshToken_success() throws InterruptedException {
    // given
    log.info("[refreshToken_success] 테스트 시작");
    String email = "test";
    String role = "USER";
    Long userId = 1L;
    String deviceId = "device123";

    // 기존 refreshToken 생성 (가짜로 토큰 생성해서 저장되어 있다고 가정)
    String oldRefreshToken = jwtTokenProvider.createRefreshToken(email, role, userId, deviceId);

    RefreshToken savedToken = RefreshToken.builder()
        .emailId(email)
        .deviceId(deviceId)
        .token(oldRefreshToken)
        .expiryDate(new Date())
        .build();

    User user = User.builder()
        .id(userId)
        .emailId(email)
        .password("encodedPass")
        .role(Role.USER)
        .build();

    // when: Mock Stubbing
    when(refreshTokenRepository.findByEmailIdAndDeviceId(email, deviceId)).thenReturn(Optional.of(savedToken));
    when(userRepository.findByEmailId(email)).thenReturn(Optional.of(user));
    when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Thread.sleep(3000);

    // 실제 refreshToken 로직 호출
    TokenResponseDto response = authService.refreshToken(oldRefreshToken, deviceId);

    // then
    log.info("[refreshToken_success] 발급된 AccessToken: {}", response.getAccessToken());
    log.info("[refreshToken_success] 발급된 RefreshToken: {}", response.getRefreshToken());

    // 새 토큰이 발급되었는지 확인 (이전 토큰과 다름)
    assertThat(response.getRefreshToken()).isNotEqualTo(oldRefreshToken);
    assertThat(response.getAccessToken()).isNotBlank();
    assertThat(response.getRefreshToken()).isNotBlank();

    // 저장된 토큰이 새로 바뀌었는지 확인
    verify(refreshTokenRepository).save(argThat(token ->
        token.getEmailId().equals(email) &&
            token.getDeviceId().equals(deviceId) &&
            token.getToken().equals(response.getRefreshToken())
    ));

    log.info("[refreshToken_success] 테스트 종료");
  }

    @Test
    @DisplayName("기기별로 다른 RefreshToken 저장 테스트")
    void saveRefreshToken_perDevice() {
      // given
      String email = "test@example.com";
      String password = "encodedPass";
      String role = "USER";
      Long userId = 1L;
      String deviceId1 = "deviceA";
      String deviceId2 = "deviceB";

      User user = User.builder()
          .id(userId)
          .emailId(email)
          .password(password)
          .role(Role.USER)
          .build();

      String refreshToken1 = "refreshTokenA";
      String refreshToken2 = "refreshTokenB";
      String accessToken1 = "accessTokenA";
      String accessToken2 = "accessTokenB";

      when(userRepository.findByEmailId(email)).thenReturn(Optional.of(user));
      when(jwtTokenProvider.createAccessToken(email, role, userId)).thenReturn(accessToken1, accessToken2);
      when(jwtTokenProvider.createRefreshToken(email, role, userId, deviceId1)).thenReturn(refreshToken1);
      when(jwtTokenProvider.createRefreshToken(email, role, userId, deviceId2)).thenReturn(refreshToken2);
      when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

      // when
      LoginRequestDto request1 = new LoginRequestDto(email, password, deviceId1);
      LoginRequestDto request2 = new LoginRequestDto(email, password, deviceId2);

      authService.login(request1);
      authService.login(request2);

      // then
      verify(refreshTokenRepository, times(2)).save(tokenCaptor.capture());

      List<RefreshToken> savedTokens = tokenCaptor.getAllValues();
      for (RefreshToken token : savedTokens) {
        System.out.println("=== 저장된 토큰 ===");
        System.out.println("Email    : " + token.getEmailId());
        System.out.println("DeviceId : " + token.getDeviceId());
        System.out.println("Token    : " + token.getToken());
      }

      assertThat(savedTokens).hasSize(2);
      assertThat(savedTokens).extracting("deviceId")
          .containsExactlyInAnyOrder(deviceId1, deviceId2);
    }





  @Test
  void refreshToken_invalidToken_throwsException() {
    log.info("[refreshToken_invalidToken_throwsException] 테스트 시작");
    when(jwtTokenProvider.validateToken("invalid-token")).thenReturn(false);

    assertThatThrownBy(() -> authService.refreshToken("invalid-token", "device123"))
        .isInstanceOf(BadCredentialsException.class)
        .hasMessageContaining("Invalid Refresh Token");

    log.info("[refreshToken_invalidToken_throwsException] 테스트 종료");
  }

  @Test
  void logout_success() {
    log.info("[logout_success] 테스트 시작");
    String email = "test@example.com";
    Long userId = 1L;
    String deviceId = "device123";

    // 실제 JwtTokenProvider를 사용하여 토큰 생성
    String refreshToken = jwtTokenProvider.createRefreshToken(email, Role.USER.name(), userId, deviceId);

    RefreshToken savedToken = RefreshToken.builder()
        .emailId(email)
        .deviceId(deviceId)
        .token(refreshToken)
        .expiryDate(new Date())
        .build();

    when(jwtTokenProvider.validateToken(refreshToken)).thenCallRealMethod();
    when(jwtTokenProvider.getEmailIdFromJWT(refreshToken)).thenCallRealMethod();
    when(refreshTokenRepository.findByEmailIdAndDeviceId(email, deviceId)).thenReturn(
        Optional.of(savedToken));
    doNothing().when(refreshTokenRepository).delete(savedToken);

    authService.logout(refreshToken, deviceId);

    verify(refreshTokenRepository).delete(savedToken);
    log.info("[logout_success] 테스트 종료");
  }


  @Test
  void logout_invalidToken_throwsException() {
    log.info("[logout_invalidToken_throwsException] 테스트 시작");
    when(jwtTokenProvider.validateToken("invalid-token")).thenReturn(false);

    assertThatThrownBy(() -> authService.logout("invalid-token", "device123"))
        .isInstanceOf(BadCredentialsException.class)
        .hasMessageContaining("Invalid Refresh Token");

    log.info("[logout_invalidToken_throwsException] 테스트 종료");
  }
  */
}


package com.playblog.userservice.auth.service;

import com.playblog.userservice.auth.dto.LoginRequestDto;
import com.playblog.userservice.auth.dto.TokenResponseDto;
import com.playblog.userservice.auth.entity.RefreshToken;
import com.playblog.userservice.auth.jwt.JwtTokenProvider;
import com.playblog.userservice.auth.repository.RefreshTokenRepository;
import com.playblog.userservice.user.User;
import com.playblog.userservice.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;

  @Transactional
  public TokenResponseDto login(LoginRequestDto loginRequest) {
    User user = userRepository.findByEmailId(loginRequest.getEmailId())
        .orElseThrow(() -> new BadCredentialsException("Invalid email"));

    if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
      throw new BadCredentialsException("Invalid password");
    }

    String emailId = user.getEmailId();
    String role = user.getRole().name();
    Long userId = user.getId();
    String deviceId = loginRequest.getDeviceId();  // ← DTO에서 받아옴

    // AccessToken, RefreshToken 생성
    String accessToken = jwtTokenProvider.createAccessToken(emailId, role, userId);
    String refreshToken = jwtTokenProvider.createRefreshToken(emailId, role, userId, deviceId);


    // 기존 deviceId로 저장된 RefreshToken 제거 (멀티 디바이스 중복 방지)
    refreshTokenRepository.deleteByEmailIdAndDeviceId(emailId, deviceId);

    // 새 RefreshToken 저장
    RefreshToken tokenEntity = RefreshToken.builder()
        .emailId(user.getEmailId())
        .deviceId(deviceId)
        .token(refreshToken)
        .expiryDate(
            new Date(System.currentTimeMillis()
                + jwtTokenProvider.getRefreshExpiration())
        )
        .build();

    refreshTokenRepository.save(tokenEntity);

    return TokenResponseDto.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  @Transactional
  public TokenResponseDto refreshToken(String refreshToken, String deviceId) {
    // 토큰 유효성 검사 (서명, 만료 등)
    if (!jwtTokenProvider.validateToken(refreshToken)) {
      throw new BadCredentialsException("Invalid Refresh Token");
    }

    // JWT 내부에서 emailId 추출
    String emailId = jwtTokenProvider.getEmailIdFromJWT(refreshToken);

    // 저장된 토큰 조회 (email + device 조합)
    RefreshToken savedToken = refreshTokenRepository.findByEmailIdAndDeviceId(emailId, deviceId)
        .orElseThrow(() -> new BadCredentialsException("Refresh Token not found"));

    // 전달받은 토큰과 DB 저장 토큰이 일치하는지 확인
    if (!savedToken.getToken().equals(refreshToken)) {
      throw new BadCredentialsException("Refresh Token mismatch");
    }

    // 유저 정보 조회
    User user = userRepository.findByEmailId(emailId)
        .orElseThrow(() -> new BadCredentialsException("User not found"));

    // 새 토큰 생성
    String newAccessToken = jwtTokenProvider.createAccessToken(emailId, user.getRole().name(), user.getId());
    String newRefreshToken = jwtTokenProvider.createRefreshToken(emailId, user.getRole().name(), user.getId(), deviceId);

    // 토큰 갱신
    savedToken.updateToken(
        newRefreshToken,
        new Date(System.currentTimeMillis() + jwtTokenProvider.getRefreshExpiration())
    );
    refreshTokenRepository.save(savedToken);


    return TokenResponseDto.builder()
        .accessToken(newAccessToken)
        .refreshToken(newRefreshToken)
        .build();
  }


  @Transactional
  public void logout(String refreshToken, String deviceId) {
    // 토큰 유효성 검사
    if (!jwtTokenProvider.validateToken(refreshToken)) {
      throw new BadCredentialsException("Invalid Refresh Token");
    }

    // JWT에서 이메일 추출
    String emailId = jwtTokenProvider.getEmailIdFromJWT(refreshToken);

    // 저장된 토큰이 있는지 확인 (디바이스 기준)
    RefreshToken savedToken = refreshTokenRepository.findByEmailIdAndDeviceId(emailId, deviceId)
        .orElseThrow(() -> new BadCredentialsException("Refresh Token not found"));

    // 저장된 토큰과 전달받은 토큰이 일치하는지 확인
    if (!savedToken.getToken().equals(refreshToken)) {
      throw new BadCredentialsException("Refresh Token mismatch");
    }

    // 리프레시 토큰 삭제 (해당 디바이스만 로그아웃)
    refreshTokenRepository.delete(savedToken);
  }

}

package com.playblog.userservice.auth.controller;


import com.playblog.userservice.common.response.ApiResponse;
import com.playblog.userservice.auth.service.AuthService;
import com.playblog.userservice.auth.dto.LoginRequestDto;
import com.playblog.userservice.auth.dto.RefreshTokenRequestDto;
import com.playblog.userservice.auth.dto.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<TokenResponseDto>> login(@RequestBody LoginRequestDto request) {
    TokenResponseDto token = authService.login(request);
    return ResponseEntity.ok(ApiResponse.success(token));
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<TokenResponseDto>> refreshToken(
      @RequestBody RefreshTokenRequestDto request
  ){
    TokenResponseDto response = authService.reissueAccessToken(request.getRefreshToken(), request.getDeviceId());
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(@RequestBody RefreshTokenRequestDto request) {
    authService.logout(request.getRefreshToken(), request.getDeviceId());
    return ResponseEntity.ok(ApiResponse.success(null));
  }

}
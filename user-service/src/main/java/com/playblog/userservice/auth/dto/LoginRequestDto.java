package com.playblog.userservice.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class LoginRequestDto {
  private final String emailId;
  private final String password;
  private final String deviceId;
}

package com.playblog.userservice.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RefreshTokenRequestDto {
    private final String refreshToken;
    private final String deviceId;

}

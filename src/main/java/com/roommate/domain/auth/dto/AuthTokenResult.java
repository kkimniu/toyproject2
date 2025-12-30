package com.roommate.domain.auth.dto;

import com.roommate.domain.auth.dto.response.LoginResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthTokenResult {
    private final LoginResponse loginResponse;
    private final String refreshToken;
    private final long refreshMaxAgeSeconds;
}

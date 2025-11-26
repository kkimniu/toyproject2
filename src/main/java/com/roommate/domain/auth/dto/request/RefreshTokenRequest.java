package com.roommate.domain.auth.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RefreshTokenRequest {
    @NotBlank(message = "refresh_token은 필수 값입니다.")
    private String refreshToken;
}

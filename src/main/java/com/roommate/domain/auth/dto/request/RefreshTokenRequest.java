package com.roommate.domain.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class RefreshTokenRequest {
    @NotBlank(message = "refresh_token은 필수 값입니다.")
    private String refreshToken;
}

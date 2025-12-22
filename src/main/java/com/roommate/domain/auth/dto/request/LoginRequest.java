package com.roommate.domain.auth.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "잘못된 이메일 입니다")
    private String email;
    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 8, max = 64, message = "비밀번호는 8자에서 64자 이하로 입력해주세요")
    private String password;
}

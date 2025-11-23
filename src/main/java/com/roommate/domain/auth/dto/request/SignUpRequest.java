package com.roommate.domain.auth.dto.request;

import com.roommate.domain.member.entity.MemberDrinkingEnum;
import com.roommate.domain.member.entity.MemberSmokingEnum;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class SignUpRequest {
    private Long workTypeId;
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "잘못된 이메일 입니다")
    private String email;
    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 8, max = 64, message = "비밀번호는 8자에서 64자 이하로 입력해주세요")
    private String password;
    @NotBlank(message = "이름을 입력해주세요")
    private String name;
    @Pattern(regexp = "^(01[0-9]-?\\d{3,4}-?\\d{4})$", message = "올바른 전화번호 형식이 아닙니다.")
    private String phone;
    @Size(max = 255)
    private String photoUrl;
    private String sleepTime;
    private MemberSmokingEnum smoking;
    private MemberDrinkingEnum drinking;
    private String mbti;
}

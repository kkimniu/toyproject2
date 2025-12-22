package com.roommate.domain.auth.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.roommate.domain.member.entity.MemberDrinkingEnum;
import com.roommate.domain.member.entity.MemberSmokingEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
@NoArgsConstructor
public class SignUpRequest {
    @NotNull(message = "직업/라이프스타일을 선택해주세요")
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

    @NotNull(message = "흡연 여부를 선택해주세요")
    private MemberSmokingEnum smoking;

    @NotNull(message = "음주 여부를 선택해주세요")
    private MemberDrinkingEnum drinking;
    private String mbti;

    private List<Long> hobbyIds;
    private List<Long> preferenceIds;
    private List<Long> petIds;

    private String signupKey;
    private Long profileTempFileId;
}

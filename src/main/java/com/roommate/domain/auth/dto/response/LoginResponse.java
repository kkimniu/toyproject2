package com.roommate.domain.auth.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.roommate.domain.member.entity.MemberDrinkingEnum;
import com.roommate.domain.member.entity.MemberGenderEnum;
import com.roommate.domain.member.entity.MemberRoleEnum;
import com.roommate.domain.member.entity.MemberSmokingEnum;
import com.roommate.domain.member.entity.MemberStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class LoginResponse {
    private Long memberId;
    private Long workTypeId;
    private String email;
    private String name;
    private String phone;
    private String photoUrl;
    private MemberGenderEnum gender;
    private LocalDate birthDate;
    private String sleepTime;
    private MemberSmokingEnum smoking;
    private MemberDrinkingEnum drinking;
    private String mbti;
    private MemberRoleEnum memberRoleEnum;
    private MemberStatusEnum status;
    private LocalDateTime bannedUntil;

    private String accessToken;
    private String tokenType;
}

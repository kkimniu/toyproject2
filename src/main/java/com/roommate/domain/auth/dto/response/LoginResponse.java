package com.roommate.domain.auth.dto.response;

import com.roommate.domain.member.entity.MemberDrinkingEnum;
import com.roommate.domain.member.entity.MemberRoleEnum;
import com.roommate.domain.member.entity.MemberSmokingEnum;
import lombok.Data;

@Data
public class LoginResponse {
    private Long memberId;
    private Long workTypeId;
    private String email;
    private String name;
    private String phone;
    private String photoUrl;
    private String sleepTime;
    private MemberSmokingEnum smoking;
    private MemberDrinkingEnum drinking;
    private String mbti;
    private MemberRoleEnum memberRoleEnum;

    private String accessToken;
    private String refreshToken;
    private String tokenType;
}

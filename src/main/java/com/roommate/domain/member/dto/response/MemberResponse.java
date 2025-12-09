package com.roommate.domain.member.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.roommate.domain.member.entity.MemberDrinkingEnum;
import com.roommate.domain.member.entity.MemberRoleEnum;
import com.roommate.domain.member.entity.MemberSmokingEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class MemberResponse {
    private Long memberId;
    private Long workTypeId;
    private String workTypeName;
    private String email;
    private String name;
    private String phone;
    private String photoUrl;
    private String sleepTime;
    private MemberSmokingEnum smoking;
    private MemberDrinkingEnum drinking;
    private String mbti;
    private MemberRoleEnum memberRoleEnum;

    // 프로필 상세에 필요한 목록들
    private List<HobbyResponse> hobbies;
    private List<PreferenceResponse> preferences;
    private List<PetResponse> pets;
}

package com.roommate.domain.member.dto.response;

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
    private MemberGenderEnum gender;
    private LocalDate birthDate;
    private String sleepTime;
    private MemberSmokingEnum smoking;
    private MemberDrinkingEnum drinking;
    private String mbti;
    private MemberRoleEnum memberRoleEnum;
    private MemberStatusEnum status;
    private LocalDateTime bannedUntil;

    // 프로필 상세에 필요한 목록들
    private List<HobbyResponse> hobbies;
    private List<PreferenceResponse> preferences;
    private List<PetResponse> pets;
}

package com.roommate.domain.member.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.roommate.domain.member.entity.MemberDrinkingEnum;
import com.roommate.domain.member.entity.MemberSmokingEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
@NoArgsConstructor
public class MemberProfileUpdateRequest {
    private String name;
    private String phone;
    private String photoUrl;

    private Long workTypeId;
    private String sleepTime;

    private MemberSmokingEnum smoking;
    private MemberDrinkingEnum drinking;
    private String mbti;

    private List<Long> hobbyIds;
    private List<Long> preferenceIds;
    private List<Long> petIds;
}

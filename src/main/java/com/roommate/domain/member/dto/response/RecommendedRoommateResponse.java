package com.roommate.domain.member.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class RecommendedRoommateResponse {
    private final Long roomId;
    private final String roomTitle;
    private final Long memberId;
    private final String name;
    private final Integer age;
    private final String workTypeName;
    private final String location;
    private final String intro;
    private final String budget;
    private final String rating;
    private final String imageUrl;
    private final List<String> tags;
}

package com.roommate.domain.community.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class CommunityPostListItemResponse {
    private Long communityPostId;
    private Long memberId;
    private String memberName;
    private String title;
    private String contentPreview;
    private int views;
    private LocalDateTime createdAt;
}

package com.roommate.domain.community.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class CommunityPostResponse {
    private Long communityPostId;
    private Long memberId;
    private String memberName;
    private String title;
    private String content;
    private int views;
    private boolean owner;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

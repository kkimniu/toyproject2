package com.roommate.domain.community.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostEntity {
    private Long communityPostId;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private String title;
    private String content;
    private int views;
    private boolean blinded;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

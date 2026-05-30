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
public class CommunityCommentEntity {
    private Long communityCommentId;
    private Long communityPostId;
    private Long parentCommentId;
    private Long memberId;
    private String memberName;
    private String content;
    private boolean blinded;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

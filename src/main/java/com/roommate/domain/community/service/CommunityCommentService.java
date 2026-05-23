package com.roommate.domain.community.service;

import com.roommate.domain.community.dto.CommunityCommentRequest;
import com.roommate.domain.community.dto.CommunityCommentResponse;

import java.util.List;

public interface CommunityCommentService {
    List<CommunityCommentResponse> getComments(Long communityPostId, Long currentMemberId);

    CommunityCommentResponse createComment(Long communityPostId, Long memberId, CommunityCommentRequest request);

    void deleteComment(Long communityCommentId, Long memberId);
}

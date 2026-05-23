package com.roommate.domain.community.service;

import com.roommate.domain.community.dto.CommunityPostListResponse;
import com.roommate.domain.community.dto.CommunityPostRequest;
import com.roommate.domain.community.dto.CommunityPostResponse;

public interface CommunityPostService {
    CommunityPostListResponse getPosts(int page, int size, String keyword);

    CommunityPostResponse getPost(Long communityPostId, Long currentMemberId, boolean countView);

    CommunityPostResponse createPost(Long memberId, CommunityPostRequest request);

    CommunityPostResponse updatePost(Long communityPostId, Long memberId, CommunityPostRequest request);

    void deletePost(Long communityPostId, Long memberId);
}

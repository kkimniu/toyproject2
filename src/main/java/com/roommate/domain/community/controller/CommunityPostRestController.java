package com.roommate.domain.community.controller;

import com.roommate.common.security.UserDetailsImpl;
import com.roommate.domain.community.dto.CommunityPostListResponse;
import com.roommate.domain.community.dto.CommunityPostRequest;
import com.roommate.domain.community.dto.CommunityPostResponse;
import com.roommate.domain.community.service.CommunityPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
public class CommunityPostRestController {
    private final CommunityPostService communityPostService;

    @GetMapping
    public CommunityPostListResponse getPosts(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String keyword) {
        return communityPostService.getPosts(page, size, keyword);
    }

    @GetMapping("/{communityPostId}")
    public CommunityPostResponse getPost(@PathVariable Long communityPostId,
                                         @RequestParam(name = "count_view", defaultValue = "false") boolean countView,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long memberId = userDetails != null ? userDetails.getMemberId() : null;
        return communityPostService.getPost(communityPostId, memberId, countView);
    }

    @PostMapping
    public CommunityPostResponse createPost(@RequestBody CommunityPostRequest request,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return communityPostService.createPost(userDetails.getMemberId(), request);
    }

    @PatchMapping("/{communityPostId}")
    public CommunityPostResponse updatePost(@PathVariable Long communityPostId,
                                            @RequestBody CommunityPostRequest request,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return communityPostService.updatePost(communityPostId, userDetails.getMemberId(), request);
    }

    @DeleteMapping("/{communityPostId}")
    public void deletePost(@PathVariable Long communityPostId,
                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        communityPostService.deletePost(communityPostId, userDetails.getMemberId());
    }
}

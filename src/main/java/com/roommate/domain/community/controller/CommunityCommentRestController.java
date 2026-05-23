package com.roommate.domain.community.controller;

import com.roommate.common.security.UserDetailsImpl;
import com.roommate.domain.community.dto.CommunityCommentRequest;
import com.roommate.domain.community.dto.CommunityCommentResponse;
import com.roommate.domain.community.service.CommunityCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityCommentRestController {
    private final CommunityCommentService communityCommentService;

    @GetMapping("/posts/{communityPostId}/comments")
    public List<CommunityCommentResponse> getComments(@PathVariable Long communityPostId,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long memberId = userDetails != null ? userDetails.getMemberId() : null;
        return communityCommentService.getComments(communityPostId, memberId);
    }

    @PostMapping("/posts/{communityPostId}/comments")
    public CommunityCommentResponse createComment(@PathVariable Long communityPostId,
                                                  @RequestBody CommunityCommentRequest request,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return communityCommentService.createComment(communityPostId, userDetails.getMemberId(), request);
    }

    @DeleteMapping("/comments/{communityCommentId}")
    public void deleteComment(@PathVariable Long communityCommentId,
                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        communityCommentService.deleteComment(communityCommentId, userDetails.getMemberId());
    }
}

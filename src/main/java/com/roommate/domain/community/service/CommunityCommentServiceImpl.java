package com.roommate.domain.community.service;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.community.dto.CommunityCommentRequest;
import com.roommate.domain.community.dto.CommunityCommentResponse;
import com.roommate.domain.community.entity.CommunityCommentEntity;
import com.roommate.domain.community.repository.CommunityCommentRepository;
import com.roommate.domain.community.repository.CommunityPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityCommentServiceImpl implements CommunityCommentService {
    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityPostRepository communityPostRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CommunityCommentResponse> getComments(Long communityPostId, Long currentMemberId) {
        communityPostRepository.findById(communityPostId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
        return communityCommentRepository.findByPostId(communityPostId)
                .stream()
                .map((comment) -> toResponse(comment, currentMemberId))
                .toList();
    }

    @Override
    @Transactional
    public CommunityCommentResponse createComment(Long communityPostId, Long memberId, CommunityCommentRequest request) {
        communityPostRepository.findById(communityPostId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
        String content = request == null ? "" : String.valueOf(request.getContent()).trim();
        if (content.isEmpty()) {
            throw new ApiException(ErrorCode.MISSING_REQUIRED_VALUE);
        }
        Long parentCommentId = request == null ? null : request.getParentCommentId();
        if (parentCommentId != null) {
            CommunityCommentEntity parent = communityCommentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
            if (!communityPostId.equals(parent.getCommunityPostId()) || parent.getParentCommentId() != null) {
                throw new ApiException(ErrorCode.INVALID_REQUEST);
            }
        }
        CommunityCommentEntity comment = CommunityCommentEntity.builder()
                .communityPostId(communityPostId)
                .parentCommentId(parentCommentId)
                .memberId(memberId)
                .content(content)
                .build();
        communityCommentRepository.insert(comment);
        return communityCommentRepository.findById(comment.getCommunityCommentId())
                .map((saved) -> toResponse(saved, memberId))
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Override
    @Transactional
    public void deleteComment(Long communityCommentId, Long memberId) {
        int updated = communityCommentRepository.softDelete(communityCommentId, memberId);
        if (updated != 1) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    private CommunityCommentResponse toResponse(CommunityCommentEntity comment, Long currentMemberId) {
        return new CommunityCommentResponse(
                comment.getCommunityCommentId(),
                comment.getCommunityPostId(),
                comment.getParentCommentId(),
                comment.getMemberId(),
                comment.getMemberName(),
                comment.getContent(),
                currentMemberId != null && currentMemberId.equals(comment.getMemberId()),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}

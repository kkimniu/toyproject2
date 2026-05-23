package com.roommate.domain.community.service;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.community.dto.CommunityPostListItemResponse;
import com.roommate.domain.community.dto.CommunityPostListResponse;
import com.roommate.domain.community.dto.CommunityPostRequest;
import com.roommate.domain.community.dto.CommunityPostResponse;
import com.roommate.domain.community.entity.CommunityPostEntity;
import com.roommate.domain.community.repository.CommunityPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityPostServiceImpl implements CommunityPostService {
    private final CommunityPostRepository communityPostRepository;

    @Override
    @Transactional(readOnly = true)
    public CommunityPostListResponse getPosts(int page, int size, String keyword) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 50);
        String normalizedKeyword = normalize(keyword);
        long total = communityPostRepository.count(normalizedKeyword);
        List<CommunityPostListItemResponse> items = communityPostRepository.findAll(
                normalizedKeyword,
                safeSize,
                (safePage - 1) * safeSize
        ).stream().map(this::toListItem).toList();
        return new CommunityPostListResponse(items, safePage, safeSize, total, totalPages(total, safeSize));
    }

    @Override
    @Transactional
    public CommunityPostResponse getPost(Long communityPostId, Long currentMemberId, boolean countView) {
        if (countView && currentMemberId != null) {
            communityPostRepository.insertPostView(communityPostId, currentMemberId);
            communityPostRepository.syncPostViewCount(communityPostId);
        }
        return communityPostRepository.findById(communityPostId)
                .map((post) -> toResponse(post, currentMemberId))
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Override
    @Transactional
    public CommunityPostResponse createPost(Long memberId, CommunityPostRequest request) {
        validate(request);
        CommunityPostEntity post = CommunityPostEntity.builder()
                .memberId(memberId)
                .title(request.getTitle().trim())
                .content(request.getContent().trim())
                .build();
        communityPostRepository.insert(post);
        return getPost(post.getCommunityPostId(), memberId, false);
    }

    @Override
    @Transactional
    public CommunityPostResponse updatePost(Long communityPostId, Long memberId, CommunityPostRequest request) {
        validate(request);
        CommunityPostEntity existing = communityPostRepository.findById(communityPostId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!existing.getMemberId().equals(memberId)) {
            throw new ApiException(ErrorCode.NOT_ALLOWED_OPERATION);
        }
        existing.setTitle(request.getTitle().trim());
        existing.setContent(request.getContent().trim());
        communityPostRepository.update(existing);
        return getPost(communityPostId, memberId, false);
    }

    @Override
    @Transactional
    public void deletePost(Long communityPostId, Long memberId) {
        int updated = communityPostRepository.softDelete(communityPostId, memberId);
        if (updated != 1) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    private void validate(CommunityPostRequest request) {
        if (request == null || normalize(request.getTitle()) == null || normalize(request.getContent()) == null) {
            throw new ApiException(ErrorCode.MISSING_REQUIRED_VALUE);
        }
    }

    private CommunityPostListItemResponse toListItem(CommunityPostEntity post) {
        return new CommunityPostListItemResponse(
                post.getCommunityPostId(),
                post.getMemberId(),
                post.getMemberName(),
                post.getTitle(),
                preview(post.getContent()),
                post.getViews(),
                post.getCreatedAt()
        );
    }

    private CommunityPostResponse toResponse(CommunityPostEntity post, Long currentMemberId) {
        return new CommunityPostResponse(
                post.getCommunityPostId(),
                post.getMemberId(),
                post.getMemberName(),
                post.getTitle(),
                post.getContent(),
                post.getViews(),
                currentMemberId != null && currentMemberId.equals(post.getMemberId()),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    private String preview(String value) {
        String normalized = normalize(value);
        if (normalized == null) return "";
        return normalized.length() <= 120 ? normalized : normalized.substring(0, 120) + "...";
    }

    private String normalize(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private int totalPages(long total, int size) {
        return total == 0 ? 0 : (int) Math.ceil((double) total / size);
    }
}

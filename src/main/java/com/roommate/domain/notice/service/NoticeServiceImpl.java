package com.roommate.domain.notice.service;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.notice.dto.NoticeListResponse;
import com.roommate.domain.notice.dto.NoticeRequest;
import com.roommate.domain.notice.dto.NoticeResponse;
import com.roommate.domain.notice.entity.NoticeEntity;
import com.roommate.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
    private final NoticeRepository noticeRepository;

    @Override
    @Transactional(readOnly = true)
    public NoticeListResponse getPublicNotices(int page, int size, String keyword) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 50);
        String normalizedKeyword = normalize(keyword);
        long total = noticeRepository.countPublic(normalizedKeyword);
        List<NoticeResponse> items = noticeRepository.findPublic(
                normalizedKeyword,
                safeSize,
                (safePage - 1) * safeSize
        ).stream().map(this::toResponse).toList();
        return new NoticeListResponse(items, safePage, safeSize, total, totalPages(total, safeSize));
    }

    @Override
    @Transactional(readOnly = true)
    public NoticeResponse getPublicNotice(Long noticeId) {
        return noticeRepository.findPublicById(noticeId)
                .map(this::toResponse)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public NoticeListResponse getAdminNotices(int page, int size, String keyword, Boolean published) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 50);
        String normalizedKeyword = normalize(keyword);
        long total = noticeRepository.countForAdmin(normalizedKeyword, published);
        List<NoticeResponse> items = noticeRepository.findForAdmin(
                normalizedKeyword,
                published,
                safeSize,
                (safePage - 1) * safeSize
        ).stream().map(this::toResponse).toList();
        return new NoticeListResponse(items, safePage, safeSize, total, totalPages(total, safeSize));
    }

    @Override
    @Transactional(readOnly = true)
    public NoticeResponse getAdminNotice(Long noticeId) {
        return noticeRepository.findForAdminById(noticeId)
                .map(this::toResponse)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Override
    @Transactional
    public NoticeResponse createNotice(Long adminId, NoticeRequest request) {
        validate(request);
        NoticeEntity notice = NoticeEntity.builder()
                .adminId(adminId)
                .title(request.getTitle().trim())
                .content(request.getContent().trim())
                .pinned(Boolean.TRUE.equals(request.getPinned()))
                .published(request.getPublished() == null || request.getPublished())
                .build();
        noticeRepository.insert(notice);
        return getAdminNotice(notice.getNoticeId());
    }

    @Override
    @Transactional
    public NoticeResponse updateNotice(Long noticeId, NoticeRequest request) {
        validate(request);
        NoticeEntity existing = noticeRepository.findForAdminById(noticeId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
        existing.setTitle(request.getTitle().trim());
        existing.setContent(request.getContent().trim());
        existing.setPinned(Boolean.TRUE.equals(request.getPinned()));
        existing.setPublished(request.getPublished() == null || request.getPublished());
        noticeRepository.update(existing);
        return getAdminNotice(noticeId);
    }

    @Override
    @Transactional
    public void deleteNotice(Long noticeId) {
        int updated = noticeRepository.softDelete(noticeId);
        if (updated != 1) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    private void validate(NoticeRequest request) {
        if (request == null || normalize(request.getTitle()) == null || normalize(request.getContent()) == null) {
            throw new ApiException(ErrorCode.MISSING_REQUIRED_VALUE);
        }
    }

    private String normalize(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private int totalPages(long total, int size) {
        return total == 0 ? 0 : (int) Math.ceil((double) total / size);
    }

    private NoticeResponse toResponse(NoticeEntity notice) {
        return new NoticeResponse(
                notice.getNoticeId(),
                notice.getTitle(),
                notice.getContent(),
                notice.isPinned(),
                notice.isPublished(),
                notice.getCreatedAt(),
                notice.getUpdatedAt()
        );
    }
}

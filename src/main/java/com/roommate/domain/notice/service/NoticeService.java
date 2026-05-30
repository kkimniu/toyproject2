package com.roommate.domain.notice.service;

import com.roommate.domain.notice.dto.NoticeListResponse;
import com.roommate.domain.notice.dto.NoticeRequest;
import com.roommate.domain.notice.dto.NoticeResponse;

public interface NoticeService {
    NoticeListResponse getPublicNotices(int page, int size, String keyword);
    NoticeResponse getPublicNotice(Long noticeId);
    NoticeListResponse getAdminNotices(int page, int size, String keyword, Boolean published);
    NoticeResponse getAdminNotice(Long noticeId);
    NoticeResponse createNotice(Long adminId, NoticeRequest request);
    NoticeResponse updateNotice(Long noticeId, NoticeRequest request);
    void deleteNotice(Long noticeId);
}

package com.roommate.domain.notification.service;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.notification.dto.NotificationResponse;
import com.roommate.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImp implements NotificationService{
    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public long countUnread(Long memberId) {
        if (memberId == null) {
            throw new ApiException(ErrorCode.AUTH_REQUIRED);
        }
        return notificationRepository.countUnreadByMemberId(memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getRecentNotifications(Long memberId, int limit) {
        if (memberId == null) {
            throw new ApiException(ErrorCode.AUTH_REQUIRED);
        }
        int safeLimit = Math.min(Math.max(limit, 1), 20);
        return notificationRepository.findRecentByMemberId(memberId, safeLimit);
    }

    @Override
    @Transactional
    public void markAsRead(Long memberId, Long notificationId) {
        if (memberId == null || notificationId == null) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }
        notificationRepository.markAsRead(memberId, notificationId);
    }
}

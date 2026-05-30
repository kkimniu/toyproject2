package com.roommate.domain.notification.service;

import com.roommate.domain.notification.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
    long countUnread(Long memberId);

    List<NotificationResponse> getRecentNotifications(Long memberId, int limit);

    void markAsRead(Long memberId, Long notificationId);
}

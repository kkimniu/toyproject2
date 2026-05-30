package com.roommate.domain.notification.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.roommate.domain.notification.dto.NotificationResponse;

import java.util.List;

@Mapper
public interface NotificationRepository {
    void deleteByMemberId(@Param("memberId") Long memberId);

    void insertChatNotificationIfEnabled(
            @Param("memberId") Long memberId,
            @Param("chatRoomId") Long chatRoomId,
            @Param("message") String message
    );

    void insertReportResolutionNotification(@Param("memberId") Long memberId,
                                            @Param("reportId") Long reportId,
                                            @Param("message") String message);

    long countUnreadByMemberId(@Param("memberId") Long memberId);

    List<NotificationResponse> findRecentByMemberId(@Param("memberId") Long memberId,
                                                    @Param("limit") int limit);

    int markAsRead(@Param("memberId") Long memberId,
                   @Param("notificationId") Long notificationId);
}

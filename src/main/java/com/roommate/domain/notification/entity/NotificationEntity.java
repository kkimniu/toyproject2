package com.roommate.domain.notification.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationEntity {
    private Long notificationId;
    private Long memberId;
    private String type;
    private Long referenceId;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
}

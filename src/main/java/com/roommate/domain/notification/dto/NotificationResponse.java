package com.roommate.domain.notification.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class NotificationResponse {
    private Long notificationId;
    private String type;
    private Long referenceId;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
}

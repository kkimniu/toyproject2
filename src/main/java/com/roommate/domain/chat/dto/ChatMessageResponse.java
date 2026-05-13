package com.roommate.domain.chat.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class ChatMessageResponse {
    private final Long messageId;
    private final Long chatRoomId;
    private final Long senderId;
    private final String senderName;
    private final String message;
    private final LocalDateTime sentAt;
    private final boolean mine;
}

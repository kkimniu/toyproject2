package com.roommate.domain.chat.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class ChatMessageResponse {
    private Long messageId;
    private Long chatRoomId;
    private Long senderId;
    private String message;
    private LocalDateTime sentAt;
}

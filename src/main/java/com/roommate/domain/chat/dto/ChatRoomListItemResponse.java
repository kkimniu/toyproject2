package com.roommate.domain.chat.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class ChatRoomListItemResponse {
    private final Long chatRoomId;
    private final Long roomId;
    private final String roomTitle;
    private final Long partnerId;
    private final String partnerName;
    private final String partnerPhotoUrl;
    private final String lastMessage;
    private final LocalDateTime lastMessageAt;
}

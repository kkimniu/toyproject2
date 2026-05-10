package com.roommate.domain.chat.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomListItemResponse {
    private Long chatRoomId;
    private Long roomId;
    private String roomTitle;
    private String roomThumbnailUrl;

    private Long otherMemberId;
    private String otherName;
    private String otherPhotoUrl;

    private String lastMessage;
    private LocalDateTime lastSentAt;

    private Integer unreadCount;
}

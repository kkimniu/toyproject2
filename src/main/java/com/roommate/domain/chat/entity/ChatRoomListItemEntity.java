package com.roommate.domain.chat.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomListItemEntity {
    private Long chatRoomId;
    private Long roomId;
    private String roomTitle;
    private Long partnerId;
    private String partnerName;
    private String partnerPhotoUrl;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
}

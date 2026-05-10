package com.roommate.domain.chat.entity;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ChatMessageEntity {
    private Long messageId;
    private Long chatRoomId;
    private Long senderId;
    private String message;
    private LocalDateTime sentAt;
    private boolean deletedBySender;
    private boolean deletedByReceiver;
}

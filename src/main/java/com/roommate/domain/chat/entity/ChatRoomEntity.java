package com.roommate.domain.chat.entity;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ChatRoomEntity {
    private Long chatRoomId;
    private Long roomId ;
    private Long ownerId;
    private Long partnerId;
    private boolean deletedByOwner;
    private boolean deletedByPartner;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

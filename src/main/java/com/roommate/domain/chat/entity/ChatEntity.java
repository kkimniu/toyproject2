package com.roommate.domain.chat.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatEntity {
    private Long chatRoomId;
    private Long roomId;
    private Long ownerId;
    private Long partnerId;
    private int deletedByOwner;
    private int deletedByPartner;
}

package com.roommate.domain.room.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomStatusEnum {
    OPEN("OPEN"),
    RESERVED("RESERVED"),
    CLOSED("CLOSED"),
    HIDDEN("HIDDEN");

    private final String RoomStatus;
}

package com.roommate.domain.room.entity;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RoomTypeEntity {
    private Long roomTypeId;
    private String roomTypeName;
    private LocalDateTime createdAt;
}

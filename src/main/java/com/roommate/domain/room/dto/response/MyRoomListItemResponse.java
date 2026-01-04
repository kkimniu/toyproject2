package com.roommate.domain.room.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.roommate.domain.room.entity.RoomStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class MyRoomListItemResponse {
    private final Long roomId;
    private final String roomTitle;
    private final String address;
    private final Double monthlyRent;
    private final Double deposit;
    private final RoomStatusEnum status;
    private final String thumbnailUrl;
    private final LocalDateTime roomCreatedAt;
}

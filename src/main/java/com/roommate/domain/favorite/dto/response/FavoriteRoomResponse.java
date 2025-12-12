package com.roommate.domain.favorite.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.roommate.domain.room.entity.RoomStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class FavoriteRoomResponse {
    private final Long roomId;
    private final String roomTitle;
    private final Double deposit;
    private final Double monthlyRent;
    private final RoomStatusEnum status;
    private final String thumbnailUrl;
}

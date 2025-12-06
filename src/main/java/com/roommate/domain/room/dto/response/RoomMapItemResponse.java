package com.roommate.domain.room.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.roommate.domain.room.entity.RoomStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class RoomMapItemResponse {
    private final Long roomId;
    private final Double lat;
    private final Double lng;
    private final Double monthlyRent;
    private final Double deposit;
    private final RoomStatusEnum status;
    private final String thumbnailUrl;
}

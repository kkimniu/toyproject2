package com.roommate.domain.room.dto.request;

import com.roommate.domain.room.entity.RoomStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoomStatusUpdateRequest {
    private RoomStatusEnum status;
}

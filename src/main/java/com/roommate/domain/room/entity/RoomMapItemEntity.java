package com.roommate.domain.room.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomMapItemEntity {
    private Long roomId;
    private String roomTitle;
    private Double lat;
    private Double lng;
    private String address;
    private Double monthlyRent;
    private Double deposit;
    private RoomStatusEnum status;
    private String thumbnailUrl;
}

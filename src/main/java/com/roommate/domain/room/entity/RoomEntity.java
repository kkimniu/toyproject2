package com.roommate.domain.room.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RoomEntity {
    private Long roomId;
    private Long memberId;
    private String roomTitle;
    private String roomContent;
    private Long roomTypeId;
    private Double monthlyRent;
    private Double deposit;
    private Float areaM2;
    private Integer floor;
    private String address;
    private String legalDong;
    private String landNumber;
    private Double lat;
    private Double lng;
    private LocalDate availableFrom;
    private Integer maxRoommates;
    private Integer views;
    private Integer interestCount;
    private RoomStatusEnum status;
    private boolean deleted;
    private LocalDateTime roomCreatedAt;
    private LocalDateTime roomUpdatedAt;
}

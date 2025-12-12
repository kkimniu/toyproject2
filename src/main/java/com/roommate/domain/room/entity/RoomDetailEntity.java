package com.roommate.domain.room.entity;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDetailEntity {
    private Long roomId;
    private String title;
    private String content;
    private Long roomTypeId;
    private String roomTypeName;
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
    private LocalDateTime roomCreatedAt;
    private LocalDateTime roomUpdatedAt;
    // 작성자
    private Long ownerId;
    private String ownerNickname;
    private String ownerPhotoUrl;
}

package com.roommate.domain.room.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RoomCreateRequest {
    private String title;
    private String content;
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
    private List<String> imageUrls;
}

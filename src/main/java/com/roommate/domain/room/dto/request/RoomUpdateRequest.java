package com.roommate.domain.room.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
@NoArgsConstructor
public class RoomUpdateRequest {
    private String roomTitle;
    private String content;
    private Long roomTypeId;
    private Double monthlyRent;
    private Double deposit;
    private Float areaM2;
    private Integer floor;
    private String address;
    private String legalDong;
    private String landNumber;
    private LocalDate availableFrom;
    private Integer maxRoommates;
    private List<String> imageUrls;
    private List<Long> tempFileIds;
}

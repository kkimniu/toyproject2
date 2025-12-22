package com.roommate.domain.room.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
@NoArgsConstructor
public class RoomCreateRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotNull
    private Long roomTypeId;
    @NotNull
    @PositiveOrZero
    private Double monthlyRent;
    @NotNull
    @PositiveOrZero
    private Double deposit;
    private Float areaM2;
    private Integer floor;
    @NotBlank
    private String address;
    private String legalDong;
    private String landNumber;
    private LocalDate availableFrom;
    private Integer maxRoommates;
    private List<String> imageUrls;
    private List<Long> tempFileIds;
}

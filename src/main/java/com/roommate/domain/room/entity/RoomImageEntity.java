package com.roommate.domain.room.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RoomImageEntity {
    private Long imageId;
    private Long roomId;
    private String imageUrl;
    private Integer sortOrder;
    @Builder.Default
    private boolean deleted = false;
    private LocalDateTime createdAt;
}

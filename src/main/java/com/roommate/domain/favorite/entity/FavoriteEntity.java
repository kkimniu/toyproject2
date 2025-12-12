package com.roommate.domain.favorite.entity;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FavoriteEntity {
    private Long favoriteId;
    private Long roomId;
    private Long memberId;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

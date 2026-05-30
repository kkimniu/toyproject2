package com.roommate.domain.auth.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TokenRefreshEntity {
    private Long tokenId;
    private Long memberId;
    private String refreshTokenHash;
    private LocalDateTime expiresAt;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

package com.roommate.domain.file.entity;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TempUploadFileEntity {
    private Long tempFileId;
    private Long memberId;
    private String signupKey;
    private String originalName;
    private String tempPath;
    private Integer used;
    private LocalDateTime createdAt;
}

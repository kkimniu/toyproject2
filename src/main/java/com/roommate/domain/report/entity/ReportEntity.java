package com.roommate.domain.report.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportEntity {
    private Long reportId;
    private Long reporterId;
    private Long roomId;
    private Long chatRoomId;
    private Long communityPostId;
    private Long communityCommentId;
    private Long targetMemberId;
    private String reportType;
    private String reason;
}

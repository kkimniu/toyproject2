package com.roommate.admin.service;

import com.roommate.admin.dto.AdminReportListItemResponse;
import com.roommate.admin.dto.AdminReportTargetModerationRequest;
import com.roommate.domain.member.entity.MemberRoleEnum;

public interface AdminReportTargetModerationService {
    AdminReportListItemResponse deleteReportedMember(
            Long reportId,
            AdminReportTargetModerationRequest request,
            Long adminId,
            MemberRoleEnum adminRole
    );

    AdminReportListItemResponse deleteReportedChatRoom(
            Long reportId,
            AdminReportTargetModerationRequest request,
            Long adminId
    );
}

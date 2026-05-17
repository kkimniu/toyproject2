package com.roommate.admin.service;

import com.roommate.admin.dto.AdminDashboardSummaryResponse;
import com.roommate.domain.member.entity.MemberStatusEnum;
import com.roommate.domain.member.repository.MemberRepository;
import com.roommate.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;

    @Override
    public AdminDashboardSummaryResponse getSummary() {
        return new AdminDashboardSummaryResponse(
                memberRepository.countMembersForAdmin(),
                memberRepository.countMembersByStatusForAdmin(MemberStatusEnum.BANNED),
                reportRepository.countReportsByStatusForAdmin("PENDING"),
                reportRepository.countReportsByStatusForAdmin("RESOLVED")
        );
    }
}

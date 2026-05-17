package com.roommate.domain.report.service;

import com.roommate.domain.report.dto.MyReportListItemResponse;
import com.roommate.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportSerivceImp implements ReportService {

    private final ReportRepository reportRepository;

    @Override
    public List<MyReportListItemResponse> getMyReports(Long reporterId) {
        return reportRepository.findMyReports(reporterId);
    }
}

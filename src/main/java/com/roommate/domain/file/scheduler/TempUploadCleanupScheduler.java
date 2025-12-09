package com.roommate.domain.file.scheduler;

import com.roommate.domain.file.service.TempUploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TempUploadCleanupScheduler {

    private final TempUploadFileService tempUploadFileService;

    /**
     * 매 시간마다 6시간 이상 지난 미사용 temp 파일 정리
     */
    @Scheduled(cron = "0 0 * * * *") // 매 정시
    public void cleanup() {
        tempUploadFileService.cleanupExpiredTempFiles(6);
    }
}

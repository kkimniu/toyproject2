package com.roommate.domain.file.scheduler;

import com.roommate.domain.file.service.RoomFileGcService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomImageGcScheduler {
    private final RoomFileGcService roomFileGcService;

    @Scheduled(cron = "0 30 3 * * *") // 매일 새벽 3:30
    public void cleanupRoomOrphans() {
        roomFileGcService.cleanupOrphanRoomFiles();
    }
}

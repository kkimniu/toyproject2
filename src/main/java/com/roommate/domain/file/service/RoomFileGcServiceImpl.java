package com.roommate.domain.file.service;

import com.roommate.domain.room.repository.RoomImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoomFileGcServiceImpl implements RoomFileGcService{

    private final RoomImageRepository roomImageRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public void cleanupOrphanRoomFiles() {
        // 1) DB에서 참조 중인 url들
        List<String> referenced = roomImageRepository.findAllImageUrls();
        Set<String> referencedSet = new HashSet<>(referenced);

        // 2) 디스크에 존재하는 room 파일들
        List<String> diskUrls = fileStorageService.listRoomImageUrls(); // 위에 추가한 메서드

        // 3) 디스크에는 있는데 DB에 없으면 삭제 대상
        for (String url : diskUrls) {
            if (!referencedSet.contains(url)) {
                fileStorageService.deleteByUrl(url);
            }
        }
    }
}

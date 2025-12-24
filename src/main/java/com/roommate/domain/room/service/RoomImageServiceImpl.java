package com.roommate.domain.room.service;

import com.roommate.domain.file.service.TempUploadFileService;
import com.roommate.domain.room.repository.RoomImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomImageServiceImpl implements RoomImageService {
    private final RoomImageRepository roomImageRepository;
    private final TempUploadFileService tempUploadFileService;

    @Override
    @Transactional
    public void attachTempImagesToRoom(Long roomId, Long memberId, List<Long> tempFileIds) {
        if (tempFileIds == null || tempFileIds.isEmpty()) {
            // 이미지 없는 케이스: 기존 이미지도 없을 거라 그냥 종료
            return;
        }

        List<String> imageUrls = new ArrayList<>();

        for (Long tempFileId : tempFileIds) {
            // 왜: 남의 temp 파일 도용 방지 + used=1 확정
            String url = tempUploadFileService.useTempFileForRoom(tempFileId, memberId, roomId);
            imageUrls.add(url);
        }

        replaceRoomImages(roomId, imageUrls);
    }

    @Override
    @Transactional
    public void replaceRoomImages(Long roomId, List<String> imageUrls) {
        // 1) 기존 이미지 전체 삭제
        roomImageRepository.deleteByRoomId(roomId);

        // 2) 새 이미지가 없으면 그대로 종료
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        // 3) 새 이미지 순서대로 저장 (0부터 sort_order)
        for (int i = 0; i < imageUrls.size(); i++) {
            roomImageRepository.insertRoomImage(roomId, imageUrls.get(i), i);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findImageUrlsByRoomId(Long roomId) {
        return roomImageRepository.findImageUrlsByRoomId(roomId);
    }

    @Override
    @Transactional
    public void updateRoomImages(Long roomId, Long memberId, List<String> keepImageUrls, List<Long> tempFileIds) {
        List<String> finalUrls = new ArrayList<>();

        // 1) 유지할 기존 이미지
        if (keepImageUrls != null && !keepImageUrls.isEmpty()) {
            finalUrls.addAll(keepImageUrls);
        }

        // 2) 새 temp 이미지 확정
        if (tempFileIds != null && !tempFileIds.isEmpty()) {
            for (Long tempFileId : tempFileIds) {
                String url = tempUploadFileService.useTempFileForRoom(tempFileId, memberId, roomId);
                finalUrls.add(url);
            }
        }

        // 3) 최종 목록으로 교체(삭제/추가 모두 반영)
        replaceRoomImages(roomId, finalUrls);
    }
}
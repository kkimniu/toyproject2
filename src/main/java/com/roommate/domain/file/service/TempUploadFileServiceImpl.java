package com.roommate.domain.file.service;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.file.entity.TempUploadFileEntity;
import com.roommate.domain.file.repository.TempUploadFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TempUploadFileServiceImpl implements TempUploadFileService {

    private final FileStorageService fileStorageService;
    private final TempUploadFileRepository tempUploadFileRepository;

    @Override
    @Transactional
    public TempUploadFileEntity uploadTempProfileImageForSignup(String signupKey, MultipartFile file) {
        if (signupKey == null || signupKey.trim().isEmpty()) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }

        if (signupKey.length() != 36) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }

        try {
            java.util.UUID.fromString(signupKey);
        } catch (IllegalArgumentException e) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }

        String url = fileStorageService.storeTempImage("profile", file);

        TempUploadFileEntity tempUploadFileEntity = new TempUploadFileEntity();
        tempUploadFileEntity.setMemberId(null);
        tempUploadFileEntity.setSignupKey(signupKey);
        tempUploadFileEntity.setOriginalName(file.getOriginalFilename());
        tempUploadFileEntity.setTempPath(url);
        tempUploadFileEntity.setUsed(0);

        tempUploadFileRepository.save(tempUploadFileEntity);
        return tempUploadFileEntity;
    }

    @Override
    @Transactional
    public String useTempFileForSignup(Long tempFileId, String signupKey, Long memberId) {
        TempUploadFileEntity tempUploadFileEntity = tempUploadFileRepository.findByIdAndSignupKey(tempFileId, signupKey);
        if (tempUploadFileEntity == null) throw new ApiException(ErrorCode.FILE_NOT_FOUND);
        if (tempUploadFileEntity.getUsed() != null && tempUploadFileEntity.getUsed() == 1) throw new ApiException(ErrorCode.FILE_ALREADY_USED);
        String finalUrl = fileStorageService.moveTempProfileToProfile(memberId, tempUploadFileEntity.getTempPath());
        tempUploadFileRepository.deleteById(tempFileId);
        return finalUrl;
    }

    @Override
    @Transactional
    public TempUploadFileEntity uploadTempProfileImage(Long memberId, MultipartFile file) {
        String url = fileStorageService.storeTempImage("profile", file);
        TempUploadFileEntity tempUploadFileEntity = new TempUploadFileEntity();
        tempUploadFileEntity.setMemberId(memberId);
        tempUploadFileEntity.setSignupKey(null);
        tempUploadFileEntity.setOriginalName(file.getOriginalFilename());
        tempUploadFileEntity.setTempPath(url);
        tempUploadFileEntity.setUsed(0);
        tempUploadFileRepository.save(tempUploadFileEntity);
        return tempUploadFileEntity;
    }

    @Override
    @Transactional
    public TempUploadFileEntity uploadTempRoomImage(Long memberId, MultipartFile file) {
        String url = fileStorageService.storeTempImage("room", file);
        TempUploadFileEntity tempUploadFileEntity = new TempUploadFileEntity();
        tempUploadFileEntity.setMemberId(memberId);
        tempUploadFileEntity.setSignupKey(null);
        tempUploadFileEntity.setOriginalName(file.getOriginalFilename());
        tempUploadFileEntity.setTempPath(url);
        tempUploadFileEntity.setUsed(0);
        tempUploadFileRepository.save(tempUploadFileEntity);
        return tempUploadFileEntity;
    }

    @Override
    @Transactional
    public void cleanupExpiredTempFiles(int hours) {
        LocalDateTime threshold = LocalDateTime.now().minusHours(hours);
        List<TempUploadFileEntity> expiredList = tempUploadFileRepository.findExpired(threshold);

        for (TempUploadFileEntity e : expiredList) {
            fileStorageService.deleteByUrl(e.getTempPath());
            tempUploadFileRepository.deleteById(e.getTempFileId());
        }
    }

    @Override
    @Transactional
    public String useTempFileForRoom(Long tempFileId, Long memberId, Long roomId) {
        TempUploadFileEntity tempUploadFile = tempUploadFileRepository.findByIdAndMemberId(tempFileId, memberId);
        if (tempUploadFile == null) throw new ApiException(ErrorCode.FILE_NOT_FOUND);
        if (tempUploadFile.getUsed() != null && tempUploadFile.getUsed() == 1) throw new ApiException(ErrorCode.FILE_ALREADY_USED);
        String finalUrl = fileStorageService.moveTempRoomToRoom(roomId, tempUploadFile.getTempPath());
        tempUploadFileRepository.deleteById(tempFileId);
        return finalUrl;
    }

    @Override
    @Transactional
    public void deleteTempFile(Long tempFileId, String signupKey) {
        TempUploadFileEntity tempUploadFileEntity = tempUploadFileRepository.findByIdAndSignupKey(tempFileId, signupKey);
        if (tempUploadFileEntity == null) return; // 없으면 그냥 종료(프론트 UX상 OK)

        fileStorageService.deleteByUrl(tempUploadFileEntity.getTempPath()); // 실제 파일 삭제
        tempUploadFileRepository.deleteById(tempFileId); // DB 삭제
    }
}

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
    public TempUploadFileEntity uploadTempProfileImage(MultipartFile file) {
        String url = fileStorageService.storeTempImage("profile", file);

        TempUploadFileEntity tempUploadFileEntity = new TempUploadFileEntity();
        tempUploadFileEntity.setOriginalName(file.getOriginalFilename());
        tempUploadFileEntity.setTempPath(url);
        tempUploadFileEntity.setUsed(0);
        tempUploadFileRepository.save(tempUploadFileEntity);
        return tempUploadFileEntity;
    }

    @Override
    @Transactional
    public String useTempFile(Long tempFileId) {
        TempUploadFileEntity tempUploadFileEntity = tempUploadFileRepository.findById(tempFileId);
        if (tempUploadFileEntity == null) {
            throw new ApiException(ErrorCode.FILE_NOT_FOUND);
        }
        if (tempUploadFileEntity.getUsed() != null && tempUploadFileEntity.getUsed() == 1) {
            throw new ApiException(ErrorCode.FILE_ALREADY_USED);
        }

        tempUploadFileEntity.setUsed(1);
        tempUploadFileRepository.updateUsed(tempUploadFileEntity);
        return tempUploadFileEntity.getTempPath();
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
}

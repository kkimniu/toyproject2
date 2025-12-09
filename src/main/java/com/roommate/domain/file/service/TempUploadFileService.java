package com.roommate.domain.file.service;

import com.roommate.domain.file.entity.TempUploadFileEntity;
import org.springframework.web.multipart.MultipartFile;

public interface TempUploadFileService {

    public TempUploadFileEntity uploadTempProfileImage(MultipartFile file);

    public String useTempFile(Long tempFileId);

    public void cleanupExpiredTempFiles(int hours);
}

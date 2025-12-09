package com.roommate.domain.file.controller;

import com.roommate.domain.file.dto.response.TempUploadFileResponse;
import com.roommate.domain.file.entity.TempUploadFileEntity;
import com.roommate.domain.file.service.TempUploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadRestController {

    private final TempUploadFileService tempUploadFileService;

    @PutMapping("/temp/profile")
    public TempUploadFileResponse uploadTempProfileImage(@RequestParam("file") MultipartFile file) {
        TempUploadFileEntity tempUploadFileEntity = tempUploadFileService.uploadTempProfileImage(file);
        return new TempUploadFileResponse(tempUploadFileEntity.getTempFileId(), tempUploadFileEntity.getTempPath());
    }
}

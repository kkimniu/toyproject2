package com.roommate.domain.file.controller;

import com.roommate.common.security.UserDetailsImpl;
import com.roommate.domain.file.dto.response.TempUploadFileResponse;
import com.roommate.domain.file.entity.TempUploadFileEntity;
import com.roommate.domain.file.service.TempUploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadRestController {

    private final TempUploadFileService tempUploadFileService;

    @PutMapping("/temp/profile-signup")
    public TempUploadFileResponse uploadTempProfileForSignup(@RequestParam("signup_key") String signupKey, @RequestParam("file") MultipartFile file) {
        TempUploadFileEntity e = tempUploadFileService.uploadTempProfileImageForSignup(signupKey, file);
        return new TempUploadFileResponse(e.getTempFileId(), e.getTempPath());
    }

    @PutMapping("/temp/profile")
    public TempUploadFileResponse uploadTempProfileImage(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("file") MultipartFile file) {
        TempUploadFileEntity tempUploadFileEntity = tempUploadFileService.uploadTempProfileImage(userDetails.getMemberId(), file);
        return new TempUploadFileResponse(tempUploadFileEntity.getTempFileId(), tempUploadFileEntity.getTempPath());
    }

    @PutMapping("/temp/room")
    public TempUploadFileResponse uploadTempRoomImage(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("file") MultipartFile file) {
        TempUploadFileEntity tempUploadFileEntity = tempUploadFileService.uploadTempRoomImage(userDetails.getMemberId(), file);
        return new TempUploadFileResponse(tempUploadFileEntity.getTempFileId(), tempUploadFileEntity.getTempPath());
    }
    // FileUploadRestController에 추가
    @DeleteMapping("/temp")
    public ResponseEntity<Void> deleteTempFile(@RequestParam("signup_key") String signupKey, @RequestParam("temp_file_id") Long tempFileId) {
        tempUploadFileService.deleteTempFile(tempFileId,signupKey);
        return ResponseEntity.noContent().build();
    }
}

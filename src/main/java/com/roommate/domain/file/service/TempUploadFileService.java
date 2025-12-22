package com.roommate.domain.file.service;

import com.roommate.domain.file.entity.TempUploadFileEntity;
import org.springframework.web.multipart.MultipartFile;

public interface TempUploadFileService {

    //회원가입전
    public TempUploadFileEntity uploadTempProfileImageForSignup(String signupKey, MultipartFile file);

    public String useTempFileForSignup(Long tempFileId, String signupKey, Long memberId);

    public void deleteTempFile(Long tempFileId, String signupKey);
    //로그인후
    public TempUploadFileEntity uploadTempProfileImage(Long memberId, MultipartFile file);

    public TempUploadFileEntity uploadTempRoomImage(Long memberId, MultipartFile file);

    public void cleanupExpiredTempFiles(int hours);

    String useTempFileForRoom(Long tempFileId, Long memberId, Long roomId);

}

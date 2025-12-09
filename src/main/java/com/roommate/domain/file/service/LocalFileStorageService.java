package com.roommate.domain.file.service;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

    @Value("${file.upload.root-path}")
    private String uploadRootPath;

    @Override
    public String storeProfileImg(Long memberId, MultipartFile multipartFile) {
        return storeImage("profile", "member-" + memberId, multipartFile);
    }

    @Override
    public String storeRoomImage(Long roomId, MultipartFile multipartFile) {
        return storeImage("room", "room-" + roomId, multipartFile);
    }

    private String storeImage(String subDir, String prefix, MultipartFile file) {

        // ✅ 1. 파일 존재 여부
        if (file == null) {
            throw new ApiException(ErrorCode.FILE_NOT_FOUND);
        }

        // ✅ 2. 파일 비어있는지
        if (file.isEmpty()) {
            throw new ApiException(ErrorCode.FILE_EMPTY);
        }

        // ✅ 3. 이미지 타입 검증
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new ApiException(ErrorCode.INVALID_IMAGE_TYPE);
        }

        // ✅ 4. 저장 디렉토리 준비
        File dir = new File(uploadRootPath, subDir);  // ./uploads/profile or ./uploads/room
        if (!dir.exists() && !dir.mkdirs()) {
            throw new ApiException(ErrorCode.FILE_PATH_INVALID);
        }

        // ✅ 5. 확장자 추출
        String originalName = file.getOriginalFilename();
        String ext = "";

        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }

        // ✅ 6. 최종 파일명 생성
        String filename = prefix + "-" + System.currentTimeMillis() + ext;
        File dest = new File(dir, filename);

        // ✅ 7. 파일 저장
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new ApiException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        // ✅ 8. 클라이언트 접근 URL 반환
        return "/upload/" + subDir + "/" + filename;
    }

    @Override
    public String storeTempImage(String category, MultipartFile multipartFile) {
        String subDir = "temp/" + category;
        String prefix = "tmp-" + category;
        return storeImage(subDir,prefix,multipartFile);
    }

    @Override
    public void deleteByUrl(String url) {
        if (url == null || !url.startsWith("/upload/")) {
            return;
        }
        String relativePath = url.substring("/upload/".length());
        File file = new File(uploadRootPath, relativePath);

        if (file.exists() && file.isFile()) {
            boolean deleted = file.delete();
            if(!deleted){
                System.out.println("프로필 이미지 삭제 실패: " + file.getAbsolutePath());
            }
        }
    }
}

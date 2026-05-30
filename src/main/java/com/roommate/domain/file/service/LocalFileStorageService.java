package com.roommate.domain.file.service;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public String moveTempProfileToProfile(Long memberId, String tempUrl) {
        return moveTempToPermanent("temp/profile", "profile", "member-" + memberId, tempUrl);
    }

    @Override
    public String moveTempRoomToRoom(Long roomId, String tempUrl) {
        return moveTempToPermanent("temp/room", "room", "room-" + roomId, tempUrl);
    }

    private String moveTempToPermanent(String expectedTempDir, String targetDir, String prefix, String tempUrl) {
        if (tempUrl == null || !tempUrl.startsWith("/upload/")) {
            throw new ApiException(ErrorCode.FILE_PATH_INVALID);
        }

        String relative = tempUrl.substring("/upload/".length()); // ex) temp/profile/xxx.png
        if (!relative.startsWith(expectedTempDir + "/")) {
            // profile temp를 room으로 옮기려는 시도 등 방지
            throw new ApiException(ErrorCode.FILE_PATH_INVALID);
        }

        File srcFile = new File(uploadRootPath, relative);
        if (!srcFile.exists() || !srcFile.isFile()) {
            throw new ApiException(ErrorCode.FILE_NOT_FOUND);
        }

        // target dir 준비
        File destDir = new File(uploadRootPath, targetDir);
        if (!destDir.exists() && !destDir.mkdirs()) {
            throw new ApiException(ErrorCode.FILE_PATH_INVALID);
        }

        // 확장자 유지
        String srcName = srcFile.getName();
        String ext = "";
        int dot = srcName.lastIndexOf(".");
        if (dot >= 0) ext = srcName.substring(dot);

        String destName = prefix + "-" + System.currentTimeMillis() + ext;
        File destFile = new File(destDir, destName);

        Path src = srcFile.toPath();
        Path dest = destFile.toPath();

        // copy + delete
        try {
            try {
                Files.move(src, dest, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                boolean deleted = srcFile.delete();
                if (!deleted) {
                    // 복사는 됐는데 원본 삭제 실패하면 temp에 남을 수 있음(나중에 정리 필요)
                    System.out.println("[WARN] temp 파일 삭제 실패: " + srcFile.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            throw new ApiException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        return "/upload/" + targetDir + "/" + destName;
    }

    @Override
    public List<String> listRoomImageUrls() {
        File dir = new File(uploadRootPath, "room");
        if (!dir.exists() || !dir.isDirectory()) return List.of();

        File[] files = dir.listFiles();
        if (files == null) return List.of();

        List<String> urls = new ArrayList<>();
        for (File f : files) {
            if (f.isFile()) {
                urls.add("/upload/room/" + f.getName());
            }
        }
        return urls;
    }
}

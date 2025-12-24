package com.roommate.domain.file.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {

    String storeProfileImg(Long memberId, MultipartFile multipartFile);

    String storeRoomImage(Long roomId, MultipartFile multipartFile);

    String storeTempImage(String category, MultipartFile multipartFile);

    void deleteByUrl(String url);

    String moveTempProfileToProfile(Long memberId, String tempUrl);

    String moveTempRoomToRoom(Long roomId, String tempUrl);

    List<String> listRoomImageUrls();
}

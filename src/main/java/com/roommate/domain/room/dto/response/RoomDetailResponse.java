package com.roommate.domain.room.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.roommate.domain.room.entity.RoomStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class RoomDetailResponse {

    private final Long roomId;
    private final String title;
    private final String content;
    private final Long roomTypeId;
    private String roomTypeName;
    private final Double monthlyRent;
    private final Double deposit;
    private final Float areaM2;
    private final Integer floor;
    private final String address;
    private final String legalDong;
    private final String landNumber;
    private final Double lat;
    private final Double lng;
    private final LocalDate availableFrom;
    private final Integer maxRoommates;
    private final Integer views;
    private final Integer interestCount;
    private final RoomStatusEnum status;
    private final LocalDateTime roomCreatedAt;
    private final LocalDateTime roomUpdatedAt;

    // 작성자 간단 프로필
    private final Long ownerId;
    private final String ownerName;
    private final String ownerPhotoUrl;

    //작성자 태그
    private final List<String> ownerTags;

    // 이미지 리스트
    private final List<String> imageUrls;

    //찜하기
    private final boolean favorited;

    //  Kakao 딥링크 (DB에 저장 X, 응답 시 계산)
    private final String kakaoMapUrl;
    private final String kakaoDirectionUrl;
    private final String kakaoRoadviewUrl;
}

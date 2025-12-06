package com.roommate.external.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoGeoPoint {
    private final double lat;
    private final double lng;
}

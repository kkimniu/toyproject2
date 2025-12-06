package com.roommate.external.kakao.service;

import com.roommate.external.kakao.dto.KakaoGeoPoint;

public interface KakaoMapService {

    public KakaoGeoPoint geocodeAddress(String address);
}

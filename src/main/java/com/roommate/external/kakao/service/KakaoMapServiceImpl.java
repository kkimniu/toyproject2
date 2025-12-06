package com.roommate.external.kakao.service;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.external.kakao.client.KakaoMapClient;
import com.roommate.external.kakao.dto.KakaoGeoPoint;
import com.roommate.external.kakao.dto.response.KakaoAddressSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoMapServiceImpl implements KakaoMapService{
    private final KakaoMapClient kakaoMapClient;

    @Override
    public KakaoGeoPoint geocodeAddress(String address) {
        KakaoAddressSearchResponse response = kakaoMapClient.searchAddress(address);

        if (response == null || response.getDocuments() == null || response.getDocuments().isEmpty()) {
            throw new ApiException(ErrorCode.KAKAO_ADDRESS_NOT_FOUND);
        }

        KakaoAddressSearchResponse.Document document = response.getDocuments().get(0);

        try {
            double lng = Double.parseDouble(document.getX());
            double lat = Double.parseDouble(document.getY());
            return new KakaoGeoPoint(lat, lng);
        } catch (NumberFormatException e) {
            throw new ApiException(ErrorCode.KAKAO_API_RESPONSE_INVALID);
        }
    }
}
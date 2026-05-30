package com.roommate.external.kakao.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class KakaoAddressSearchResponse {
    private List<Document> documents; // 여러 개의 주소 목록

    @Data
    public static class Document {
        private String addressName; // 주소 이름
        private String x; // 경도
        private String y; // 위도
    }
}

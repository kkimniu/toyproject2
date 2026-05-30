package com.roommate.external.kakao.client;

import com.roommate.external.kakao.dto.response.KakaoAddressSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class KakaoMapClient {

    @Value("${kakao.map.api.key}")
    private String kakaoApiKey;

    @Value("${kakao.local.base-url:https://dapi.kakao.com}")
    private String kakaoBaseUrl;

    // - KA 헤더 안의 origin 값을 설정에서 분리해서 관리하기 위해
    @Value("${kakao.ka.origin:roommate-service}")
    private String kakaoKaOrigin;

    private final RestTemplate restTemplate;

    public KakaoAddressSearchResponse searchAddress(String query) {



        // 2) UriComponentsBuilder에게 인코딩 맡기고, 우리는 URI 객체를 만든다
        URI uri = UriComponentsBuilder
                .fromHttpUrl(kakaoBaseUrl + "/v2/local/search/address.json")
                .queryParam("query", query) // 👉 여기에는 '생짜 한글'을 넣는다
                .encode(StandardCharsets.UTF_8) // 👉 여기서 단 한 번만 인코딩
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();

        // 왜 이렇게 Authorization을 설정하는지:
        // - 카카오 REST API는 "KakaoAK {REST_API_KEY}" 형식의 헤더를 요구함
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);


        // ★ 에러의 원인이었던 KA 헤더 추가 부분 ★
        // 왜 필요한지:
        // - 카카오 로컬 API가 "KA Header is required..." 에러를 던졌기 때문에
        // - 값 안에 os/ 또는 origin/ 이 반드시 포함돼야 함
        headers.set("KA", buildKaHeader());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // 왜 exchange를 쓰는지:
        // - HttpMethod, 헤더가 담긴 HttpEntity를 함께 보내기 위해
        ResponseEntity<KakaoAddressSearchResponse> response = restTemplate.exchange(uri, HttpMethod.GET, entity, KakaoAddressSearchResponse.class);

        return response.getBody();
    }

    // KA 헤더 문자열을 만드는 메서드를 분리한 이유:
    // - 나중에 os, sdk 버전, origin 변경 시 이 메서드만 수정하면 되도록 하기 위해
    private String buildKaHeader() {
        // sdk/1.0  : 클라이언트 버전(임의 문자열 가능)
        // os/java  : Java 환경에서 호출한다는 의미 (os 정보)
        // origin/... : 우리 서비스 이름 (어디서 호출했는지)
        return "sdk/1.0 os/java origin/" + kakaoKaOrigin;
    }
}

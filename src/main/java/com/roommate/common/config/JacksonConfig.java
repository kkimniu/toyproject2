package com.roommate.common.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.format.DateTimeFormatter;

/**
 * Spring MVC에서 @ResponseBody 또는 @RequestBody로 JSON을 주고받을 때,
 * 기본적으로 Jackson의 ObjectMapper가 사용됩니다.
 * 이 클래스는 그 ObjectMapper의 동작 방식을 커스터마이징합니다.
 *
 * 주요 설정:
 *   LocalDateTime 포맷 – "yyyy-MM-dd HH:mm:ss" 형태로 출력/파싱
 *   WRITE_DATES_AS_TIMESTAMPS 비활성화 – 타임스탬프 대신 사람이 읽을 수 있는 날짜 포맷 사용
 *   PropertyNamingStrategies.SNAKE_CASE – JSON 키를 스네이크 케이스로 변환
 *   JsonInclude.Include.NON_NULL – null 값 필드는 JSON에서 제외
 *
 * 이 설정은 Spring MVC 컨텍스트에 등록된 MappingJackson2HttpMessageConverter에서 자동으로 참조됩니다.
 */
@Configuration
public class JacksonConfig implements WebMvcConfigurer {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();

        //LocalDateTime 포맷용 지정
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(java.time.LocalDateTime.class,new LocalDateTimeSerializer(DATE_TIME_FORMATTER));
        javaTimeModule.addDeserializer(java.time.LocalDateTime.class,new LocalDateTimeDeserializer(DATE_TIME_FORMATTER));
        om.registerModule(javaTimeModule);// LocalDateTime 지원 추가
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);//ISO 포멧 출력
        om.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);//스네이크 케이스 적용
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);//Null 필드 제외
        return om;
    }

}

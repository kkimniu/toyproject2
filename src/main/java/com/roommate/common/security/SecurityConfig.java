package com.roommate.common.security;

import com.roommate.common.jwt.JwtAuthenticationFilter;
import com.roommate.common.jwt.JwtUtil;
import com.roommate.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, memberRepository);
    }

    @Bean
    @Override
    public org.springframework.security.authentication.AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Spring Security 설정 클래스
     * - JWT 기반 인증 처리
     * - Stateless 환경 구성 (세션 미사용)
     * - 인증 예외 URL 지정 (로그인/회원가입, 토큰 재발급)
     * - CORS 설정 포함 (SPA 대응)
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                // ====== 뷰(페이지) 쪽: 모두 허용 ======
                .antMatchers("/", "/index.jsp", "/main").permitAll()
                .antMatchers("/rooms/**").permitAll()      // 상세 페이지 (뷰)
                .antMatchers("/chat/**").permitAll()
                .antMatchers("/resources/**", "/favicon.ico").permitAll()
                .antMatchers("/upload/**").permitAll()
                .antMatchers("/ws/**").permitAll()
                .antMatchers("/members/**").permitAll()      // 마이페이지 (뷰)
                .antMatchers("/chats", "/chats/**").permitAll()      // 채팅 페이지 (뷰)
                .antMatchers("/notices", "/notices/**").permitAll()
                .antMatchers("/admin", "/admin/**").permitAll()

                // 2) 룸 조회용 API (지도/요약/상세 데이터) - 모두 허용
                .antMatchers(HttpMethod.GET, "/api/rooms/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/notices/**").permitAll()
                // 3) 찜 조회용 API- 허용
                .antMatchers(HttpMethod.GET, "/api/favorites/**").permitAll()
                .antMatchers(HttpMethod.GET,
                        "/api/members/recommended-roommates",
                        "/api/members/form-codes",
                        "/api/members/work-types",
                        "/api/members/hobbies",
                        "/api/members/preferences",
                        "/api/members/pets").permitAll()
                .antMatchers(HttpMethod.GET, "/api/chat/rooms/**").authenticated()

                // 4) 인증/회원가입/폼코드 등 공개 API
                .antMatchers(
                        "/api/auth/signup",
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/api/files/**").permitAll()

                // 5) 테스트/로그인 관련 뷰 페이지 - 공개
                .antMatchers("/auth/login-test", "/auth/me-test", "/auth/login").permitAll()
                // ====== 회원 전용(authenticated) ======
                .antMatchers(HttpMethod.GET, "/api/members/**").authenticated()
                .antMatchers(HttpMethod.POST, "/api/members/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/members/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/members/**").authenticated()

                // 7) 방 작성/수정/삭제 API - 로그인 필요
                .antMatchers(HttpMethod.POST, "/api/rooms/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/rooms/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/rooms/**").authenticated()

                // 8) 찜 추가/수정/삭제 API - 로그인 필요
                .antMatchers(HttpMethod.POST, "/api/favorites/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/favorites/**").authenticated()

                // 9) 채팅 API - 로그인 필요
                .antMatchers(HttpMethod.POST, "/api/chat/rooms/**").authenticated()
                .antMatchers(HttpMethod.PATCH, "/api/chat/rooms/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/chat/rooms/**").authenticated()
                .antMatchers(HttpMethod.GET, "/api/reports/**").authenticated()
                .antMatchers(HttpMethod.POST, "/api/reports/**").authenticated()
                .antMatchers(HttpMethod.GET, "/api/notifications/**").authenticated()
                .antMatchers(HttpMethod.PATCH, "/api/notifications/**").authenticated()

                // 10) 관리자 API
                .antMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")

                // 11) 그 외 나머지 요청은 전부 인증 필요
                .anyRequest().authenticated();
        http.formLogin().disable().httpBasic().disable();
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(java.util.List.of("Authorization"));

        org.springframework.web.cors.UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return urlBasedCorsConfigurationSource;
    }
}

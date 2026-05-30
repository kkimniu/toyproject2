package com.roommate.domain.auth.service;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.common.jwt.JwtUtil;
import com.roommate.domain.auth.dto.AuthTokenResult;
import com.roommate.domain.auth.dto.request.LoginRequest;
import com.roommate.domain.auth.dto.request.SignUpRequest;
import com.roommate.domain.auth.dto.response.LoginResponse;
import com.roommate.domain.auth.dto.response.SignUpResponse;
import com.roommate.domain.auth.entity.TokenRefreshEntity;
import com.roommate.domain.auth.repository.TokenRefreshRepository;
import com.roommate.domain.file.service.TempUploadFileService;
import com.roommate.domain.member.entity.MemberDrinkingEnum;
import com.roommate.domain.member.entity.MemberEntity;
import com.roommate.domain.member.entity.MemberRoleEnum;
import com.roommate.domain.member.entity.MemberSmokingEnum;
import com.roommate.domain.member.entity.MemberStatusEnum;
import com.roommate.domain.member.repository.MemberHobbyRepository;
import com.roommate.domain.member.repository.MemberPetRepository;
import com.roommate.domain.member.repository.MemberPreferenceRepository;
import com.roommate.domain.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final TempUploadFileService tempUploadFileService;

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenRefreshRepository tokenRefreshRepository;

    private final MemberHobbyRepository memberHobbyRepository;
    private final MemberPreferenceRepository memberPreferenceRepository;
    private final MemberPetRepository memberPetRepository;


    @Override
    public void validateEmailDuplication(String email) {
        memberRepository.findByEmail(email).ifPresent(member -> {
            if (member.getDeleted() == 1) {
                throw new ApiException(ErrorCode.MEMBER_DEACTIVATED);
            }
            throw new ApiException(ErrorCode.DUPLICATE_EMAIL);
        });
    }

    @Override
    public void validatePassword(String requestPassword, String memberPassword) {
        if (!passwordEncoder.matches(requestPassword, memberPassword)) {
            throw new ApiException(ErrorCode.INVALID_PASSWORD);
        }
    }

    /**
     * SignUpRequest를 MemberEntity로 변환
     */
    private MemberEntity createMemberFromSignUpRequest(SignUpRequest signUpRequest) {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setEmail(signUpRequest.getEmail());
        memberEntity.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        memberEntity.setName(signUpRequest.getName());
        memberEntity.setPhone(signUpRequest.getPhone());
        memberEntity.setPhotoUrl(signUpRequest.getPhotoUrl());
        memberEntity.setGender(signUpRequest.getGender());
        memberEntity.setBirthDate(signUpRequest.getBirthDate());
        memberEntity.setSleepTime(signUpRequest.getSleepTime());
        memberEntity.setWorkTypeId(signUpRequest.getWorkTypeId());
        // 흡연/음주 기본값 처리
        memberEntity.setSmoking(signUpRequest.getSmoking() != null ? signUpRequest.getSmoking() : MemberSmokingEnum.NON_SMOKER);
        memberEntity.setDrinking(signUpRequest.getDrinking() != null ? signUpRequest.getDrinking() : MemberDrinkingEnum.NONE);
        memberEntity.setMbti(signUpRequest.getMbti());
        return memberEntity;
    }

    /**
     * MemberEntity → SignUpResponse 변환
     */
    private SignUpResponse toSignUpResponse(MemberEntity memberEntity) {
        return new SignUpResponse(
                memberEntity.getMemberId(),
                memberEntity.getWorkTypeId(),
                memberEntity.getEmail(),
                memberEntity.getName(),
                memberEntity.getPhone(),
                memberEntity.getPhotoUrl(),
                memberEntity.getGender(),
                memberEntity.getBirthDate(),
                memberEntity.getSleepTime(),
                memberEntity.getSmoking(),
                memberEntity.getDrinking(),
                memberEntity.getMbti());
    }

    /**
     * MemberEntity,jwt → toLoginUpResponse 변환
     */
    private LoginResponse toLoginResponse(MemberEntity memberEntity, String accessToken) {
        return new LoginResponse(
                memberEntity.getMemberId(),
                memberEntity.getWorkTypeId(),
                memberEntity.getEmail(),
                memberEntity.getName(),
                memberEntity.getPhone(),
                memberEntity.getPhotoUrl(),
                memberEntity.getGender(),
                memberEntity.getBirthDate(),
                memberEntity.getSleepTime(),
                memberEntity.getSmoking() != null ? memberEntity.getSmoking() : MemberSmokingEnum.NON_SMOKER,
                memberEntity.getDrinking() != null ? memberEntity.getDrinking() : MemberDrinkingEnum.NONE,
                memberEntity.getMbti(),
                memberEntity.getRole(),
                memberEntity.getStatus(),
                memberEntity.getBannedUntil(),
                accessToken,
                "Bearer"
        );
    }

    @Override
    @Transactional
    public SignUpResponse signUp(SignUpRequest signUpRequest) {

        validateEmailDuplication(signUpRequest.getEmail());

        MemberEntity memberEntity = createMemberFromSignUpRequest(signUpRequest);
        memberRepository.save(memberEntity);
        Long memberId = memberEntity.getMemberId();

        if (signUpRequest.getProfileTempFileId() != null && signUpRequest.getSignupKey() != null) {
            String finalPhotoUrl = tempUploadFileService.useTempFileForSignup(signUpRequest.getProfileTempFileId(), signUpRequest.getSignupKey(), memberId);
            memberRepository.updatePhotoUrl(memberId, finalPhotoUrl);
        }


        if (signUpRequest.getHobbyIds() != null && !signUpRequest.getHobbyIds().isEmpty()) {
            memberHobbyRepository.insertMemberHobbies(memberId, signUpRequest.getHobbyIds());
        }

        if (signUpRequest.getPreferenceIds() != null && !signUpRequest.getPreferenceIds().isEmpty()) {
            memberPreferenceRepository.insertMemberPreferences(memberId, signUpRequest.getPreferenceIds());
        }

        if (signUpRequest.getPetIds() != null && !signUpRequest.getPetIds().isEmpty()) {
            memberPetRepository.insertMemberPets(memberId, signUpRequest.getPetIds());
        }

        return toSignUpResponse(memberEntity);
    }

    @Override
    @Transactional
    public AuthTokenResult login(LoginRequest loginRequest) {
        MemberEntity memberEntity = memberRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new ApiException(ErrorCode.INVALID_EMAIL_FORMAT));

        if (memberEntity.getDeleted() == 1) {
            // 왜 이렇게 썼는지:
            // - soft delete 된 회원은 로그인 자체를 막아서
            //   탈퇴 후 토큰 발급/서비스 이용을 차단하기 위함.
            throw new ApiException(ErrorCode.MEMBER_DEACTIVATED);
        }
        if (memberEntity.getStatus() == MemberStatusEnum.BANNED) {
            throw new ApiException(ErrorCode.MEMBER_BANNED);
        }
        if (memberEntity.getStatus() != null && memberEntity.getStatus() != MemberStatusEnum.ACTIVE) {
            throw new ApiException(ErrorCode.MEMBER_DEACTIVATED);
        }

        validatePassword(loginRequest.getPassword(), memberEntity.getPassword());
        Long memberId = memberEntity.getMemberId();

        String accessToken = jwtUtil.createAccessToken(memberId, memberEntity.getRole());
        String refreshToken = jwtUtil.createRefreshToken(memberId, memberEntity.getRole());

        long refreshMaxAgeSeconds = jwtUtil.getRefreshExpirationTime() / 1000;

        saveOrUpdateRefreshToken(memberEntity, refreshToken);

        LoginResponse loginResponse = toLoginResponse(memberEntity, accessToken);
        return new AuthTokenResult(loginResponse, refreshToken, refreshMaxAgeSeconds);
    }

    /**
     * Refresh Token 저장/갱신
     * - 회원당 1개의 Refresh Token만 유지한다.
     * - 토큰 원문은 DB에 저장하지 않고, 해시값만 저장한다.
     * - 만료 시간은 "JWT exp 클레임" 기준으로 저장한다.
     */
    private void saveOrUpdateRefreshToken(MemberEntity memberEntity, String refreshToken) {
        Long memberId = memberEntity.getMemberId();
        TokenRefreshEntity tokenRefreshEntity = tokenRefreshRepository.findByMemberId(memberId).orElse(null);

        String encodedRefreshToken = passwordEncoder.encode(refreshToken);
        Claims claims = jwtUtil.validateAndParseRefreshClaims(refreshToken);
        LocalDateTime expiresAt = claims.getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        if (tokenRefreshEntity == null) {
            tokenRefreshEntity = new TokenRefreshEntity();
            tokenRefreshEntity.setMemberId(memberId);
            tokenRefreshEntity.setRefreshTokenHash(encodedRefreshToken);
            tokenRefreshEntity.setExpiresAt(expiresAt);
            tokenRefreshRepository.save(tokenRefreshEntity);
        } else {
            tokenRefreshEntity.setRefreshTokenHash(encodedRefreshToken);
            tokenRefreshEntity.setExpiresAt(expiresAt);
            tokenRefreshRepository.updateTokenRefresh(tokenRefreshEntity);
        }
    }

    @Override
    @Transactional
    public AuthTokenResult refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ApiException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        // 1) JWT 자체 검증(서명/만료/typ=refresh)
        Claims claims;
        try {
            claims = jwtUtil.validateAndParseRefreshClaims(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new ApiException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        } catch (JwtException e) {
            throw new ApiException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        Long memberId = jwtUtil.getMemberId(claims);

        MemberEntity memberEntity = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        if (memberEntity.getDeleted() == 1) {
            throw new ApiException(ErrorCode.MEMBER_DEACTIVATED);
        }
        if (memberEntity.getStatus() == MemberStatusEnum.BANNED) {
            throw new ApiException(ErrorCode.MEMBER_BANNED);
        }
        if (memberEntity.getStatus() != null && memberEntity.getStatus() != MemberStatusEnum.ACTIVE) {
            throw new ApiException(ErrorCode.MEMBER_DEACTIVATED);
        }

        // 2) DB에 저장된 refresh와 비교 (해시 매칭)
        TokenRefreshEntity tokenRefreshEntity = tokenRefreshRepository.findByMemberId(memberId).orElseThrow(() -> new ApiException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (tokenRefreshEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        if (!passwordEncoder.matches(refreshToken, tokenRefreshEntity.getRefreshTokenHash())) {
            throw new ApiException(ErrorCode.REFRESH_TOKEN_REUSED);
        }
        // 3) 새 토큰 발급(회전)
        MemberRoleEnum role = memberEntity.getRole();

        String newAccessToken = jwtUtil.createAccessToken(memberId, role);
        String newRefreshToken = jwtUtil.createRefreshToken(memberId, role);

        // 4) DB refresh 갱신(새 refresh 해시 + 새 exp)
        saveOrUpdateRefreshToken(memberEntity, newRefreshToken);

        // 5) 바디 + 쿠키 세팅용 결과 반환
        LoginResponse loginResponse = toLoginResponse(memberEntity, newAccessToken);

        long refreshMaxAgeSeconds = jwtUtil.getRefreshExpirationTime() / 1000;

        return new AuthTokenResult(loginResponse, newRefreshToken, refreshMaxAgeSeconds);
    }

    @Override
    public void logout(Long memberId) {
        tokenRefreshRepository.deleteByMemberId(memberId);
    }
}

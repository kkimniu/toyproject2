package com.roommate.domain.auth.service;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.common.jwt.JwtUtil;
import com.roommate.domain.auth.dto.request.LoginRequest;
import com.roommate.domain.auth.dto.request.RefreshTokenRequest;
import com.roommate.domain.auth.dto.request.SignUpRequest;
import com.roommate.domain.auth.dto.response.LoginResponse;
import com.roommate.domain.auth.dto.response.SignUpResponse;
import com.roommate.domain.auth.entity.TokenRefreshEntity;
import com.roommate.domain.auth.repository.TokenRefreshRepository;
import com.roommate.domain.file.service.TempUploadFileService;
import com.roommate.domain.member.entity.MemberDrinkingEnum;
import com.roommate.domain.member.entity.MemberEntity;
import com.roommate.domain.member.entity.MemberSmokingEnum;
import com.roommate.domain.member.repository.MemberHobbyRepository;
import com.roommate.domain.member.repository.MemberPetRepository;
import com.roommate.domain.member.repository.MemberPreferenceRepository;
import com.roommate.domain.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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
            // 왜 이렇게 썼는지:
            // - soft delete 구조에서도 email UNIQUE 제약이 있기 때문에
            //   deleted = 1 이라도 동일 이메일로 재가입을 허용하지 않는다.
            if (member.getDeleted() == 1) {
                // 이미 탈퇴한 이메일로 재가입 시도
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
                memberEntity.getSleepTime(),
                memberEntity.getSmoking(),
                memberEntity.getDrinking(),
                memberEntity.getMbti());
    }

    /**
     * MemberEntity,jwt → toLoginUpResponse 변환
     */
    private LoginResponse toLoginResponse(MemberEntity memberEntity, String accessToken, String refreshToken) {
        return new LoginResponse(
                memberEntity.getMemberId(),
                memberEntity.getWorkTypeId(),
                memberEntity.getEmail(),
                memberEntity.getName(),
                memberEntity.getPhone(),
                memberEntity.getPhotoUrl(),
                memberEntity.getSleepTime(),
                memberEntity.getSmoking() != null ? memberEntity.getSmoking() : MemberSmokingEnum.NON_SMOKER,
                memberEntity.getDrinking() != null ? memberEntity.getDrinking() : MemberDrinkingEnum.NONE,
                memberEntity.getMbti(),
                memberEntity.getRole(),
                accessToken,
                refreshToken,
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
            String finalPhotoUrl = tempUploadFileService.useTempFileForSignup(signUpRequest.getProfileTempFileId(),signUpRequest.getSignupKey(),memberId);
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

        SignUpResponse response = toSignUpResponse(memberEntity);
        return response;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        MemberEntity memberEntity = memberRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new ApiException(ErrorCode.INVALID_EMAIL_FORMAT));

        if (memberEntity.getDeleted() == 1) {
            // 왜 이렇게 썼는지:
            // - soft delete 된 회원은 로그인 자체를 막아서
            //   탈퇴 후 토큰 발급/서비스 이용을 차단하기 위함.
            throw new ApiException(ErrorCode.MEMBER_DEACTIVATED);
        }

        validatePassword(loginRequest.getPassword(), memberEntity.getPassword());
        String accessToken = jwtUtil.createAccessToken(memberEntity.getMemberId(), memberEntity.getRole());
        String refreshToken = jwtUtil.createRefreshToken(memberEntity.getMemberId(), memberEntity.getRole());
        saveOrUpdateRefreshToken(memberEntity, refreshToken);

        LoginResponse loginResponse = toLoginResponse(memberEntity, accessToken, refreshToken);
        return loginResponse;
    }

    /**
     * Refresh Token 저장/갱신
     * - 회원당 1개의 Refresh Token만 유지한다.
     * - 토큰 원문은 DB에 저장하지 않고, 해시값만 저장한다.
     * - 만료 시간은 JWT의 exp 클레임을 그대로 사용한다.
     */
    private void saveOrUpdateRefreshToken(MemberEntity memberEntity, String refreshToken) {
        Long memberId = memberEntity.getMemberId();
        TokenRefreshEntity tokenRefreshEntity = tokenRefreshRepository.findByMemberId(memberId).orElse(null);
        String encodedRefreshToken = passwordEncoder.encode(refreshToken);
        //토큰 만료시간 꺼내기
        Claims claims = jwtUtil.getUserInfoFromToken(refreshToken);
        Date expirationData = claims.getExpiration();
        LocalDateTime expiresAt = expirationData.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

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
    public LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        /**
         * 1) Refresh Token 기본 유효성 검증
         *    - 서명(Signature) 검증
         *    - 파싱 오류 검증
         *    - 토큰 자체 만료 여부 검증
         *
         *    Refresh Token은 Access Token 재발급을 위한 중요한 자격 증명 수단이므로
         *    기본적인 유효성 검사 실패 시 즉시 요청을 차단한다.
         */
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new ApiException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        /**
         * 2) Refresh Token 내부의 subject(memberId) 추출
         *    - 정상적으로 서명된 Refresh Token이면 claim에서 memberId를 추출할 수 있다.
         *    - 토큰 위조 방지를 위해 DB 조회 전에 반드시 JWT에서 추출한 ID 값을 기준으로 처리한다.
         */
        Long memberId = jwtUtil.getMemberIdFromRefreshToken(refreshToken);
        MemberEntity memberEntity = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        if (memberEntity.getDeleted() == 1) {
            throw new ApiException(ErrorCode.MEMBER_DEACTIVATED);
        }
        /**
         * 3) DB에 저장된 Refresh Token 검증
         *    - 서버는 Refresh Token 원본을 저장하지 않고 Bcrypt 해시값만 저장한다.
         *    - 따라서 전달된 refreshToken과 DB에 저장된 해시를 matches()로 비교해야 한다.
         *
         *    검증 항목:
         *    1) 해당 회원의 refresh token 레코드 존재 여부
         *    2) 전달받은 refresh token과 DB 저장된 해시 비교
         *    3) Refresh Token 실제 만료 시간(DB 저장값) 확인
         */
        TokenRefreshEntity tokenRefreshEntity = tokenRefreshRepository.findByMemberId(memberId).orElseThrow(() -> new ApiException(ErrorCode.INVALID_REFRESH_TOKEN));
        if (!passwordEncoder.matches(refreshToken, tokenRefreshEntity.getRefreshTokenHash())) {
            throw new ApiException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        if (tokenRefreshEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }
        /**
         * 4) 모든 검증이 완료되었으면 새로운 Access Token 발급
         *    - Refresh Token은 그대로 유지한다(토큰 로테이션 적용 시 이 부분 변경 가능)
         *    - Access Token은 stateless 인증을 위해 매 요청마다 헤더에 첨부되어 사용됨
         */
        String newAccessToken = jwtUtil.createAccessToken(memberId, memberEntity.getRole());

        // 기존 LoginResponse 포맷 재사용하여 전달 (Refresh Token은 그대로 반환)
        return toLoginResponse(memberEntity, newAccessToken, refreshToken);
    }

    @Override
    public void logout(Long memberId) {
        tokenRefreshRepository.deleteByMemberId(memberId);
    }
}

package com.roommate.domain.auth.service;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.common.jwt.JwtUtil;
import com.roommate.domain.auth.dto.request.LoginRequest;
import com.roommate.domain.auth.dto.request.SignUpRequest;
import com.roommate.domain.auth.dto.response.LoginResponse;
import com.roommate.domain.auth.dto.response.SignUpResponse;
import com.roommate.domain.member.entity.MemberDrinkingEnum;
import com.roommate.domain.member.entity.MemberEntity;
import com.roommate.domain.member.entity.MemberSmokingEnum;
import com.roommate.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public void validateEmailDuplication(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new ApiException(ErrorCode.DUPLICATE_EMAIL);
        }
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
        memberEntity.setSmoking(signUpRequest.getSmoking());
        memberEntity.setDrinking(signUpRequest.getDrinking());
        memberEntity.setMbti(signUpRequest.getMbti());
        return memberEntity;
    }

    /**
     * MemberEntity → SignUpResponse 변환
     */
    private SignUpResponse toSignUpResponse(MemberEntity memberEntity) {
        SignUpResponse signUpResponse = new SignUpResponse();
        signUpResponse.setMemberId(memberEntity.getMemberId());
        signUpResponse.setWorkTypeId(memberEntity.getWorkTypeId());
        signUpResponse.setEmail(memberEntity.getEmail());
        signUpResponse.setName(memberEntity.getName());
        signUpResponse.setPhone(memberEntity.getPhone());
        signUpResponse.setPhotoUrl(memberEntity.getPhotoUrl());
        signUpResponse.setSleepTime(memberEntity.getSleepTime());
        signUpResponse.setSmoking(memberEntity.getSmoking());
        signUpResponse.setDrinking(memberEntity.getDrinking());
        signUpResponse.setMbti(memberEntity.getMbti());
        return signUpResponse;
    }

    /**
     * MemberEntity,jwt → toLoginUpResponse 변환
     */
    private LoginResponse toLoginResponse(MemberEntity memberEntity, String accessToken, String refreshToken) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setMemberId(memberEntity.getMemberId());
        loginResponse.setWorkTypeId(memberEntity.getWorkTypeId());
        loginResponse.setEmail(memberEntity.getEmail());
        loginResponse.setName(memberEntity.getName());
        loginResponse.setPhone(memberEntity.getPhone());
        loginResponse.setPhotoUrl(memberEntity.getPhotoUrl());
        loginResponse.setSleepTime(memberEntity.getSleepTime());
        loginResponse.setSmoking(memberEntity.getSmoking() != null ? memberEntity.getSmoking() : MemberSmokingEnum.NON_SMOKER);
        loginResponse.setDrinking(memberEntity.getDrinking() != null ? memberEntity.getDrinking() : MemberDrinkingEnum.NONE);
        loginResponse.setMbti(memberEntity.getMbti());
        loginResponse.setMemberRoleEnum(memberEntity.getRole());
        loginResponse.setAccessToken(accessToken);
        loginResponse.setRefreshToken(refreshToken);
        loginResponse.setTokenType("Bearer");
        return loginResponse;
    }

    @Override
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        validateEmailDuplication(signUpRequest.getEmail());
        MemberEntity memberEntity = createMemberFromSignUpRequest(signUpRequest);
        memberRepository.save(memberEntity);
        SignUpResponse response = toSignUpResponse(memberEntity);
        return response;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        MemberEntity memberEntity = memberRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new ApiException(ErrorCode.INVALID_EMAIL_FORMAT));
        validatePassword(loginRequest.getPassword(), memberEntity.getPassword());
        String accessToken = jwtUtil.createAccessToken(memberEntity.getMemberId(), memberEntity.getRole());
        String refreshToken = jwtUtil.createRefreshToken(memberEntity.getMemberId(), memberEntity.getRole());
        LoginResponse loginResponse = toLoginResponse(memberEntity, accessToken, refreshToken);
        return loginResponse;
    }
}

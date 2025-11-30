package com.roommate.domain.member.service;

import java.util.List;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.member.dto.response.*;
import com.roommate.domain.member.entity.*;
import com.roommate.domain.member.repository.*;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final WorkTypeRepository workTypeRepository;
    private final HobbyRepository hobbyRepository;
    private final PreferenceRepository preferenceRepository;
    private final PetRepository petRepository;

    private WorkTypeResponse toWorkTypeResponse(WorkTypeEntity workTypeEntity) {
        WorkTypeResponse workTypeResponse = new WorkTypeResponse();
        workTypeResponse.setWorkTypeId(workTypeEntity.getWorkTypeId());
        workTypeResponse.setWorkTypeName(workTypeEntity.getWorkTypeName());
        return workTypeResponse;
    }

    private HobbyResponse toHobbyResponse(HobbyEntity hobbyEntity) {
        HobbyResponse hobbyResponse = new HobbyResponse();
        hobbyResponse.setHobbyId(hobbyEntity.getHobbyId());
        hobbyResponse.setHobbyName(hobbyEntity.getHobbyName());
        return hobbyResponse;
    }

    private PreferenceResponse toPreferenceResponse(PreferenceEntity preferenceEntity) {
        PreferenceResponse preferenceResponse = new PreferenceResponse();
        preferenceResponse.setPreferenceId(preferenceEntity.getPreferenceId());
        preferenceResponse.setPreferenceName(preferenceEntity.getPreferenceName());
        return preferenceResponse;
    }

    private PetResponse toPetResponse(PetEntity petEntity) {
        PetResponse petResponse = new PetResponse();
        petResponse.setPetId(petEntity.getPetId());
        petResponse.setPetName(petEntity.getPetName());
        return petResponse;
    }

    private MemberResponse toMemberResponse(MemberEntity memberEntity, String workTypeName) {
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setWorkTypeName(workTypeName);
        memberResponse.setEmail(memberEntity.getEmail());
        memberResponse.setName(memberEntity.getName());
        memberResponse.setPhone(memberEntity.getPhone());
        memberResponse.setPhotoUrl(memberEntity.getPhotoUrl());
        memberResponse.setSleepTime(memberEntity.getSleepTime());
        memberResponse.setSmoking(memberEntity.getSmoking() != null ? memberEntity.getSmoking() : MemberSmokingEnum.NON_SMOKER);
        memberResponse.setDrinking(memberEntity.getDrinking() != null ? memberEntity.getDrinking() : MemberDrinkingEnum.NONE);
        memberResponse.setMbti(memberEntity.getMbti());
        memberResponse.setMemberRoleEnum(memberEntity.getRole());
        return memberResponse;
    }

    @Override
    public FormCodesResponse getFormCodes() {
        FormCodesResponse formCodesResponse = new FormCodesResponse();
        formCodesResponse.setWorkTypes(workTypeRepository.findAll().stream().map(this::toWorkTypeResponse).toList());
        formCodesResponse.setHobbies(hobbyRepository.findAll().stream().map(this::toHobbyResponse).toList());
        formCodesResponse.setPreferences(preferenceRepository.findAll().stream().map(this::toPreferenceResponse).toList());
        formCodesResponse.setPets(petRepository.findAll().stream().map(this::toPetResponse).toList());
        return formCodesResponse;
    }

    @Override
    public MemberResponse memberInfo(Long memberId) {
        MemberEntity memberEntity = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        WorkTypeEntity workTypeEntity = workTypeRepository.findById(memberEntity.getWorkTypeId()).orElseThrow(() -> new ApiException(ErrorCode.WORK_TYPE_NOT_FOUND));
        MemberResponse memberResponse = toMemberResponse(memberEntity, workTypeEntity.getWorkTypeName());
        return memberResponse;
    }
}

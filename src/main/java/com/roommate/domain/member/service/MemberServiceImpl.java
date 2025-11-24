package com.roommate.domain.member.service;

import java.util.List;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.member.dto.response.MemberResponse;
import com.roommate.domain.member.dto.response.WorkTypeResponse;
import com.roommate.domain.member.entity.MemberDrinkingEnum;
import com.roommate.domain.member.entity.MemberEntity;
import com.roommate.domain.member.entity.MemberSmokingEnum;
import com.roommate.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import com.roommate.domain.member.entity.WorkTypeEntity;
import com.roommate.domain.member.repository.WorkTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final WorkTypeRepository workTypeRepository;

    private WorkTypeResponse toWorkTypeResponse(WorkTypeEntity workTypeEntity) {
        WorkTypeResponse workTypeResponse = new WorkTypeResponse();
        workTypeResponse.setWorkTypeId(workTypeEntity.getWorkTypeId());
        workTypeResponse.setWorkTypeName(workTypeEntity.getWorkTypeName());
        return workTypeResponse;
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
    public List<WorkTypeResponse> findAllWorkType() {
        return workTypeRepository.findAll().stream().map(this::toWorkTypeResponse).toList();
    }

    @Override
    public MemberResponse memberInfo(Long memberId) {
        MemberEntity memberEntity = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        WorkTypeEntity workTypeEntity = workTypeRepository.findById(memberEntity.getWorkTypeId()).orElseThrow(() -> new ApiException(ErrorCode.WORK_TYPE_NOT_FOUND));
        MemberResponse memberResponse = toMemberResponse(memberEntity, workTypeEntity.getWorkTypeName());
        return memberResponse;
    }
}

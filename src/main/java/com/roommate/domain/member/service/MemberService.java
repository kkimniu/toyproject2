package com.roommate.domain.member.service;

import java.util.List;

import com.roommate.domain.member.entity.WorkTypeEntity;

public interface MemberService {

	public List<WorkTypeEntity> findAllWorkType();
}

package com.roommate.domain.member.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.roommate.domain.member.entity.WorkTypeEntity;
import com.roommate.domain.member.repository.WorkTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final WorkTypeRepository workTypeRepository; 
	
	@Override
	public List<WorkTypeEntity> findAllWorkType() {
		return workTypeRepository.findAll();
	}

}

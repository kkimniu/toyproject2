package com.roommate.domain.member.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.roommate.domain.member.entity.WorkTypeEntity;

@Mapper
public interface WorkTypeRepository {
	
	public List<WorkTypeEntity> findAll();
}

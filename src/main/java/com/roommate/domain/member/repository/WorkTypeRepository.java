package com.roommate.domain.member.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.roommate.domain.member.entity.WorkTypeEntity;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WorkTypeRepository {
	
	List<WorkTypeEntity> findAll();
	Optional<WorkTypeEntity> findById(@Param("workTypeId") Long workTypeId);
}

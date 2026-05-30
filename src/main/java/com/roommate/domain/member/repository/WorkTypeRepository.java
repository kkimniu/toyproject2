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
	Optional<WorkTypeEntity> findByName(@Param("name") String name);
	int insert(@Param("name") String name);
	int update(@Param("workTypeId") Long workTypeId, @Param("name") String name);
	int delete(@Param("workTypeId") Long workTypeId);
}

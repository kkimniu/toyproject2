package com.roommate.domain.member.repository;

import com.roommate.domain.member.entity.PreferenceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PreferenceRepository {

    List<PreferenceEntity> findAll();
    Optional<PreferenceEntity> findById(@Param("preferenceId") Long preferenceId);
    Optional<PreferenceEntity> findByName(@Param("name") String name);

    List<PreferenceEntity> findByMemberId(@Param("memberId") Long memberId);
    int insert(@Param("name") String name);
    int update(@Param("preferenceId") Long preferenceId, @Param("name") String name);
    int delete(@Param("preferenceId") Long preferenceId);
}

package com.roommate.domain.member.repository;

import com.roommate.domain.member.entity.PreferenceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PreferenceRepository {

    List<PreferenceEntity> findAll();

    List<PreferenceEntity> findByMemberId(@Param("memberId") Long memberId);
}

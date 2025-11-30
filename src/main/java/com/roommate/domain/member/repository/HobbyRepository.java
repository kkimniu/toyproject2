package com.roommate.domain.member.repository;

import com.roommate.domain.member.entity.HobbyEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HobbyRepository {

    List<HobbyEntity> findAll();

    List<HobbyEntity> findByMemberId(@Param("memberId") Long memberId);
}

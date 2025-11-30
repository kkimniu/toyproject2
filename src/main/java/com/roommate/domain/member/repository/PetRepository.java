package com.roommate.domain.member.repository;

import com.roommate.domain.member.entity.PetEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PetRepository {

    List<PetEntity> findAll();

    List<PetEntity> findByMemberId(@Param("memberId") Long memberId);
}

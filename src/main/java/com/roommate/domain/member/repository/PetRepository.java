package com.roommate.domain.member.repository;

import com.roommate.domain.member.entity.PetEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PetRepository {

    List<PetEntity> findAll();
    Optional<PetEntity> findById(@Param("petId") Long petId);
    Optional<PetEntity> findByName(@Param("name") String name);

    List<PetEntity> findByMemberId(@Param("memberId") Long memberId);
    int insert(@Param("name") String name);
    int update(@Param("petId") Long petId, @Param("name") String name);
    int delete(@Param("petId") Long petId);
}

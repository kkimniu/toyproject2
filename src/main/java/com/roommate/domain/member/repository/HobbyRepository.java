package com.roommate.domain.member.repository;

import com.roommate.domain.member.entity.HobbyEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface HobbyRepository {

    List<HobbyEntity> findAll();
    Optional<HobbyEntity> findById(@Param("hobbyId") Long hobbyId);
    Optional<HobbyEntity> findByName(@Param("name") String name);

    List<HobbyEntity> findByMemberId(@Param("memberId") Long memberId);
    int insert(@Param("name") String name);
    int update(@Param("hobbyId") Long hobbyId, @Param("name") String name);
    int delete(@Param("hobbyId") Long hobbyId);
}

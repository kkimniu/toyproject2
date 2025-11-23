package com.roommate.domain.member.repository;

import com.roommate.domain.member.entity.MemberEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface MemberRepository {

    void save(MemberEntity memberEntity);

    Optional<MemberEntity> findById(@Param("memberId") Long memberId);

    Optional<MemberEntity> findByEmail(@Param("email") String email);

    void updateMember(MemberEntity memberEntity);

}

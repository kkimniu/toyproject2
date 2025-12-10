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

    void insertMemberHobby(@Param("memberId") Long memberId, @Param("hobbyId") Long hobbyId);

    void insertMemberPreference(@Param("memberId") Long memberId, @Param("preferenceId") Long preferenceId);

    void insertMemberPet(@Param("memberId") Long memberId, @Param("petId") Long petId);

    void softDeleteMember(@Param("memberId") Long memberId);

    void updatePassword(@Param("memberId") Long memberId, @Param("password") String encodedPassword);

}

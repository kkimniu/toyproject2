package com.roommate.domain.member.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberHobbyRepository {

    void deleteByMemberId(@Param("memberId") Long memberId);

    void insertMemberHobbies(@Param("memberId") Long memberId, @Param("hobbyIds") List<Long> hobbyIds);
}

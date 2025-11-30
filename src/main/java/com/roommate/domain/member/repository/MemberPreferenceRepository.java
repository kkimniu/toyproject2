package com.roommate.domain.member.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberPreferenceRepository {

    void deleteByMemberId(@Param("memberId") Long memberId);

    void insertMemberPreferences(@Param("memberId") Long memberId, @Param("preferenceIds") List<Long> preferenceIds);
}

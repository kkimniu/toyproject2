package com.roommate.domain.member.repository;

import com.roommate.admin.dto.AdminMemberListItemResponse;
import com.roommate.domain.member.entity.MemberEntity;
import com.roommate.domain.member.entity.MemberRoleEnum;
import com.roommate.domain.member.entity.MemberStatusEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
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

    void updatePhotoUrl(@Param("memberId") Long memberId, @Param("photoUrl") String photoUrl);

    long countMembersForAdmin(@Param("keyword") String keyword,
                              @Param("role") String role,
                              @Param("status") String status,
                              @Param("from") String from,
                              @Param("to") String to);

    long countMembersByStatusForAdmin(@Param("status") MemberStatusEnum status);

    long countRepeatPenaltyMembersForAdmin(@Param("threshold") int threshold);

    List<AdminMemberListItemResponse> findMembersForAdmin(@Param("keyword") String keyword,
                                                          @Param("role") String role,
                                                          @Param("status") String status,
                                                          @Param("from") String from,
                                                          @Param("to") String to,
                                                          @Param("limit") int limit,
                                                          @Param("offset") int offset);

    int updateMemberStatusForAdmin(@Param("memberId") Long memberId, @Param("status") MemberStatusEnum status);

    int updateMemberRoleForAdmin(@Param("memberId") Long memberId, @Param("role") MemberRoleEnum role);

}

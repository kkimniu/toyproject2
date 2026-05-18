package com.roommate.admin.service;

import com.roommate.admin.dto.AdminMemberListItemResponse;
import com.roommate.admin.dto.AdminMemberListResponse;
import com.roommate.domain.member.entity.MemberRoleEnum;
import com.roommate.domain.member.entity.MemberStatusEnum;

public interface AdminMemberService {
    AdminMemberListResponse getMembers(int page,
                                       int size,
                                       String keyword,
                                       String role,
                                       String status,
                                       String from,
                                       String to);

    AdminMemberListItemResponse updateMemberStatus(Long memberId,
                                                   MemberStatusEnum status,
                                                   Long currentAdminId,
                                                   MemberRoleEnum currentAdminRole);

    AdminMemberListItemResponse updateMemberRole(Long memberId,
                                                 MemberRoleEnum role,
                                                 Long currentAdminId,
                                                 MemberRoleEnum currentAdminRole);
}

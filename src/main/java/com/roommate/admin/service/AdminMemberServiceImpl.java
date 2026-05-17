package com.roommate.admin.service;

import com.roommate.admin.dto.AdminMemberListItemResponse;
import com.roommate.admin.dto.AdminMemberListResponse;
import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.member.entity.MemberEntity;
import com.roommate.domain.member.entity.MemberRoleEnum;
import com.roommate.domain.member.entity.MemberStatusEnum;
import com.roommate.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMemberServiceImpl implements AdminMemberService {

    private final MemberRepository memberRepository;
    private final AdminActionLogService adminActionLogService;

    @Override
    public AdminMemberListResponse getMembers(int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int offset = (safePage - 1) * safeSize;

        long totalCount = memberRepository.countMembersForAdmin();
        List<AdminMemberListItemResponse> items = memberRepository.findMembersForAdmin(safeSize, offset);
        int totalPages = totalCount == 0 ? 0 : (int) Math.ceil((double) totalCount / safeSize);
        boolean hasNext = safePage < totalPages;

        return new AdminMemberListResponse(items, safePage, safeSize, totalCount, totalPages, hasNext);
    }

    @Override
    public AdminMemberListItemResponse updateMemberStatus(Long memberId,
                                                          MemberStatusEnum status,
                                                          Long currentAdminId,
                                                          MemberRoleEnum currentAdminRole) {
        if (status != MemberStatusEnum.ACTIVE && status != MemberStatusEnum.BANNED) {
            throw new ApiException(ErrorCode.ADMIN_MEMBER_STATUS_INVALID);
        }

        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.ADMIN_MEMBER_NOT_FOUND));

        if (member.getMemberId().equals(currentAdminId)) {
            throw new ApiException(ErrorCode.ADMIN_SELF_STATUS_CHANGE_NOT_ALLOWED);
        }
        if (member.getRole() == MemberRoleEnum.SUPER_ADMIN) {
            throw new ApiException(ErrorCode.ADMIN_TARGET_SUPER_ADMIN_NOT_ALLOWED);
        }
        if (member.getRole() == MemberRoleEnum.ADMIN && currentAdminRole != MemberRoleEnum.SUPER_ADMIN) {
            throw new ApiException(ErrorCode.ADMIN_TARGET_ADMIN_NOT_ALLOWED);
        }
        if (member.getStatus() == MemberStatusEnum.DELETED || member.getDeleted() == 1) {
            throw new ApiException(ErrorCode.ADMIN_MEMBER_STATUS_INVALID);
        }

        int updatedCount = memberRepository.updateMemberStatusForAdmin(memberId, status);
        if (updatedCount != 1) {
            throw new ApiException(ErrorCode.ADMIN_MEMBER_BAN_FAILED);
        }

        adminActionLogService.logMemberStatusChange(
                currentAdminId,
                memberId,
                status == MemberStatusEnum.BANNED ? "MEMBER_BANNED" : "MEMBER_UNBANNED"
        );

        member.setStatus(status);
        return new AdminMemberListItemResponse(
                member.getMemberId(),
                member.getEmail(),
                member.getName(),
                member.getRole(),
                member.getStatus(),
                member.getMemberCreatedAt()
        );
    }

    @Override
    public AdminMemberListItemResponse updateMemberRole(Long memberId,
                                                        MemberRoleEnum role,
                                                        Long currentAdminId,
                                                        MemberRoleEnum currentAdminRole) {
        if (currentAdminRole != MemberRoleEnum.SUPER_ADMIN) {
            throw new ApiException(ErrorCode.SUPER_ADMIN_ONLY);
        }
        if (role != MemberRoleEnum.USER && role != MemberRoleEnum.ADMIN) {
            throw new ApiException(ErrorCode.ADMIN_MEMBER_ROLE_INVALID);
        }

        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.ADMIN_MEMBER_NOT_FOUND));

        if (member.getMemberId().equals(currentAdminId)) {
            throw new ApiException(ErrorCode.ADMIN_SELF_ROLE_CHANGE_NOT_ALLOWED);
        }
        if (member.getRole() == MemberRoleEnum.SUPER_ADMIN) {
            throw new ApiException(ErrorCode.ADMIN_TARGET_SUPER_ADMIN_NOT_ALLOWED);
        }
        if (member.getStatus() == MemberStatusEnum.DELETED || member.getDeleted() == 1) {
            throw new ApiException(ErrorCode.ADMIN_MEMBER_ROLE_INVALID);
        }

        int updatedCount = memberRepository.updateMemberRoleForAdmin(memberId, role);
        if (updatedCount != 1) {
            throw new ApiException(ErrorCode.ADMIN_MEMBER_ROLE_INVALID);
        }

        adminActionLogService.logMemberRoleChange(
                currentAdminId,
                memberId,
                role == MemberRoleEnum.ADMIN ? "MEMBER_PROMOTED_TO_ADMIN" : "MEMBER_DEMOTED_TO_USER"
        );

        member.setRole(role);
        return new AdminMemberListItemResponse(
                member.getMemberId(),
                member.getEmail(),
                member.getName(),
                member.getRole(),
                member.getStatus(),
                member.getMemberCreatedAt()
        );
    }
}

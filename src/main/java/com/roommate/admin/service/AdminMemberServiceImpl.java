package com.roommate.admin.service;

import com.roommate.admin.dto.AdminMemberListItemResponse;
import com.roommate.admin.dto.AdminMemberListResponse;
import com.roommate.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMemberServiceImpl implements AdminMemberService {

    private final MemberRepository memberRepository;

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
}

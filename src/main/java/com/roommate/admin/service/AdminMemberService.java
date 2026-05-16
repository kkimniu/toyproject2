package com.roommate.admin.service;

import com.roommate.admin.dto.AdminMemberListResponse;

public interface AdminMemberService {
    AdminMemberListResponse getMembers(int page, int size);
}

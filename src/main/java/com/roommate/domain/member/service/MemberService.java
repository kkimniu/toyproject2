package com.roommate.domain.member.service;

import java.util.List;

import com.roommate.domain.member.dto.response.FormCodesResponse;
import com.roommate.domain.member.dto.response.MemberResponse;
import com.roommate.domain.member.dto.response.WorkTypeResponse;

public interface MemberService {

	/**
	 * 회원조회
	 */
	public MemberResponse memberInfo(Long memberId);

	/**
	 *
	 */
	public FormCodesResponse getFormCodes();
}

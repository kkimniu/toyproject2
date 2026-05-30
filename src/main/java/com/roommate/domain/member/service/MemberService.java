package com.roommate.domain.member.service;

import java.util.List;

import com.roommate.domain.member.dto.request.MemberPasswordChangeRequest;
import com.roommate.domain.member.dto.request.MemberProfileUpdateRequest;
import com.roommate.domain.member.dto.response.FormCodesResponse;
import com.roommate.domain.member.dto.response.MemberPublicResponse;
import com.roommate.domain.member.dto.response.MemberResponse;
import com.roommate.domain.member.dto.response.WorkTypeResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

	public MemberResponse memberInfo(Long memberId);

    public MemberPublicResponse memberPublicInfo(Long memberId);

	public MemberResponse updateMemberProfile(Long memberId, MemberProfileUpdateRequest request);

	void deleteMember(Long memberId);

	public FormCodesResponse getFormCodes();

	public MemberResponse updateMemberProfilePhoto(Long memberId, MultipartFile multipartFile);

	void changeMyPassword(Long memberId, MemberPasswordChangeRequest memberPasswordChangeRequest);

	public List<String> getMemberTags(Long memberId);
}

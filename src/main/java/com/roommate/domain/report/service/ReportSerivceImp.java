package com.roommate.domain.report.service;

import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.chat.entity.ChatRoomEntity;
import com.roommate.domain.chat.repository.ChatRoomRepository;
import com.roommate.domain.community.entity.CommunityCommentEntity;
import com.roommate.domain.community.entity.CommunityPostEntity;
import com.roommate.domain.community.repository.CommunityCommentRepository;
import com.roommate.domain.community.repository.CommunityPostRepository;
import com.roommate.domain.member.entity.MemberEntity;
import com.roommate.domain.member.repository.MemberRepository;
import com.roommate.domain.report.dto.MyReportListItemResponse;
import com.roommate.domain.report.dto.ReportRequest;
import com.roommate.domain.report.dto.ReportResponse;
import com.roommate.domain.report.entity.ReportEntity;
import com.roommate.domain.report.repository.ReportRepository;
import com.roommate.domain.room.entity.RoomEntity;
import com.roommate.domain.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportSerivceImp implements ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final CommunityPostRepository communityPostRepository;
    private final CommunityCommentRepository communityCommentRepository;

    @Override
    @Transactional
    public ReportResponse createMemberReport(Long reporterId, Long targetMemberId, ReportRequest request) {
        if (reporterId == null || targetMemberId == null) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }
        if (reporterId.equals(targetMemberId)) {
            throw new ApiException(ErrorCode.NOT_ALLOWED_OPERATION);
        }
        String reason = request == null ? "" : String.valueOf(request.getReason()).trim();
        if (reason.isEmpty()) {
            throw new ApiException(ErrorCode.MISSING_REQUIRED_VALUE);
        }

        MemberEntity target = memberRepository.findById(targetMemberId)
                .filter(member -> member.getDeleted() == 0)
                .orElseThrow(() -> new ApiException(ErrorCode.REPORT_TARGET_NOT_FOUND));

        if (reportRepository.existsMemberReport(reporterId, target.getMemberId())) {
            throw new ApiException(ErrorCode.REPORT_ALREADY_SUBMITTED);
        }

        ReportEntity report = ReportEntity.builder()
                .reporterId(reporterId)
                .targetMemberId(target.getMemberId())
                .reportType("MEMBER")
                .reason(reason)
                .build();

        int updated = reportRepository.saveMemberReport(report);
        if (updated != 1 || report.getReportId() == null) {
            throw new ApiException(ErrorCode.REPORT_CREATE_FAILED);
        }

        return new ReportResponse(report.getReportId(), "PENDING", "Report submitted.");
    }

    @Override
    @Transactional
    public ReportResponse createChatReport(Long reporterId, Long chatRoomId, ReportRequest request) {
        if (reporterId == null || chatRoomId == null) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }
        String reason = request == null ? "" : String.valueOf(request.getReason()).trim();
        if (reason.isEmpty()) {
            throw new ApiException(ErrorCode.MISSING_REQUIRED_VALUE);
        }

        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId);
        if (chatRoom == null) {
            throw new ApiException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }
        if (!reporterId.equals(chatRoom.getOwnerId()) && !reporterId.equals(chatRoom.getPartnerId())) {
            throw new ApiException(ErrorCode.CHAT_ACCESS_DENIED);
        }

        Long targetMemberId = reporterId.equals(chatRoom.getOwnerId())
                ? chatRoom.getPartnerId()
                : chatRoom.getOwnerId();
        if (targetMemberId == null || reporterId.equals(targetMemberId)) {
            throw new ApiException(ErrorCode.NOT_ALLOWED_OPERATION);
        }

        if (reportRepository.existsChatReport(reporterId, chatRoom.getChatRoomId())) {
            throw new ApiException(ErrorCode.REPORT_ALREADY_SUBMITTED);
        }

        ReportEntity report = ReportEntity.builder()
                .reporterId(reporterId)
                .roomId(chatRoom.getRoomId())
                .chatRoomId(chatRoom.getChatRoomId())
                .targetMemberId(targetMemberId)
                .reportType("CHAT")
                .reason(reason)
                .build();

        int updated = reportRepository.saveChatReport(report);
        if (updated != 1 || report.getReportId() == null) {
            throw new ApiException(ErrorCode.REPORT_CREATE_FAILED);
        }

        return new ReportResponse(report.getReportId(), "PENDING", "Report submitted.");
    }

    @Override
    @Transactional
    public ReportResponse createRoomReport(Long reporterId, Long roomId, ReportRequest request) {
        if (reporterId == null || roomId == null) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }
        String reason = request == null ? "" : String.valueOf(request.getReason()).trim();
        if (reason.isEmpty()) {
            throw new ApiException(ErrorCode.MISSING_REQUIRED_VALUE);
        }

        RoomEntity room = roomRepository.findById(roomId);
        if (room == null) {
            throw new ApiException(ErrorCode.REPORT_TARGET_NOT_FOUND);
        }
        if (reporterId.equals(room.getMemberId())) {
            throw new ApiException(ErrorCode.NOT_ALLOWED_OPERATION);
        }

        if (reportRepository.existsRoomReport(reporterId, room.getRoomId())) {
            throw new ApiException(ErrorCode.REPORT_ALREADY_SUBMITTED);
        }

        ReportEntity report = ReportEntity.builder()
                .reporterId(reporterId)
                .roomId(room.getRoomId())
                .targetMemberId(room.getMemberId())
                .reportType("ROOM")
                .reason(reason)
                .build();

        int updated = reportRepository.saveRoomReport(report);
        if (updated != 1 || report.getReportId() == null) {
            throw new ApiException(ErrorCode.REPORT_CREATE_FAILED);
        }

        return new ReportResponse(report.getReportId(), "PENDING", "Report submitted.");
    }

    @Override
    @Transactional
    public ReportResponse createCommunityPostReport(Long reporterId, Long communityPostId, ReportRequest request) {
        if (reporterId == null || communityPostId == null) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }
        String reason = request == null ? "" : String.valueOf(request.getReason()).trim();
        if (reason.isEmpty()) {
            throw new ApiException(ErrorCode.MISSING_REQUIRED_VALUE);
        }
        CommunityPostEntity post = communityPostRepository.findById(communityPostId)
                .orElseThrow(() -> new ApiException(ErrorCode.REPORT_TARGET_NOT_FOUND));
        if (reporterId.equals(post.getMemberId())) {
            throw new ApiException(ErrorCode.NOT_ALLOWED_OPERATION);
        }
        if (reportRepository.existsCommunityPostReport(reporterId, communityPostId)) {
            throw new ApiException(ErrorCode.REPORT_ALREADY_SUBMITTED);
        }

        ReportEntity report = ReportEntity.builder()
                .reporterId(reporterId)
                .communityPostId(communityPostId)
                .targetMemberId(post.getMemberId())
                .reportType("COMMUNITY_POST")
                .reason(reason)
                .build();

        int updated = reportRepository.saveCommunityPostReport(report);
        if (updated != 1 || report.getReportId() == null) {
            throw new ApiException(ErrorCode.REPORT_CREATE_FAILED);
        }
        return new ReportResponse(report.getReportId(), "PENDING", "Report submitted.");
    }

    @Override
    @Transactional
    public ReportResponse createCommunityCommentReport(Long reporterId, Long communityCommentId, ReportRequest request) {
        if (reporterId == null || communityCommentId == null) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }
        String reason = request == null ? "" : String.valueOf(request.getReason()).trim();
        if (reason.isEmpty()) {
            throw new ApiException(ErrorCode.MISSING_REQUIRED_VALUE);
        }
        CommunityCommentEntity comment = communityCommentRepository.findById(communityCommentId)
                .orElseThrow(() -> new ApiException(ErrorCode.REPORT_TARGET_NOT_FOUND));
        if (reporterId.equals(comment.getMemberId())) {
            throw new ApiException(ErrorCode.NOT_ALLOWED_OPERATION);
        }
        if (reportRepository.existsCommunityCommentReport(reporterId, communityCommentId)) {
            throw new ApiException(ErrorCode.REPORT_ALREADY_SUBMITTED);
        }

        ReportEntity report = ReportEntity.builder()
                .reporterId(reporterId)
                .communityPostId(comment.getCommunityPostId())
                .communityCommentId(comment.getCommunityCommentId())
                .targetMemberId(comment.getMemberId())
                .reportType("COMMUNITY_COMMENT")
                .reason(reason)
                .build();

        int updated = reportRepository.saveCommunityCommentReport(report);
        if (updated != 1 || report.getReportId() == null) {
            throw new ApiException(ErrorCode.REPORT_CREATE_FAILED);
        }
        return new ReportResponse(report.getReportId(), "PENDING", "Report submitted.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyReportListItemResponse> getMyReports(Long reporterId) {
        return reportRepository.findMyReports(reporterId);
    }

    @Override
    @Transactional(readOnly = true)
    public MyReportListItemResponse getMyReport(Long reporterId, Long reportId) {
        if (reporterId == null || reportId == null) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }
        return reportRepository.findMyReportById(reporterId, reportId)
                .orElseThrow(() -> new ApiException(ErrorCode.REPORT_TARGET_NOT_FOUND));
    }
}

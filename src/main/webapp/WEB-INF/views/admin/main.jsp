<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>RoomMate | 관리자</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/admin/main.css">
</head>
<body>
  <jsp:include page="/WEB-INF/views/common/header.jsp"/>

  <main class="admin-page">
    <section class="admin-summary" aria-label="관리자 요약 지표">
      <article class="summary-item">
        <span>전체 회원</span>
        <strong id="summaryTotalMembers">-</strong>
      </article>
      <article class="summary-item">
        <span>정지 회원</span>
        <strong id="summaryBannedMembers">-</strong>
      </article>
      <article class="summary-item">
        <span>대기 신고</span>
        <strong id="summaryPendingReports">-</strong>
      </article>
      <article class="summary-item">
        <span>처리 완료 신고</span>
        <strong id="summaryResolvedReports">-</strong>
      </article>
    </section>
    <section class="admin-heading">
      <p>관리자</p>
      <h1>운영 관리</h1>
    </section>

    <section class="admin-section" aria-label="회원 목록">
      <div class="section-header">
        <div>
          <h2>회원 관리</h2>
          <p id="adminMemberCount">회원을 불러오는 중입니다.</p>
        </div>
        <label class="page-size-control">
          <span>표시 개수</span>
          <select id="memberPageSize">
            <option value="5">5개</option>
            <option value="10">10개</option>
            <option value="20" selected>20개</option>
          </select>
        </label>
      </div>

      <form id="memberSearchForm" class="member-search-form">
        <label>
          <span>검색어</span>
          <input type="search" name="keyword" placeholder="이름 또는 이메일">
        </label>
        <label>
          <span>권한</span>
          <select name="role">
            <option value="">전체</option>
            <option value="USER">USER</option>
            <option value="ADMIN">ADMIN</option>
            <option value="SUPER_ADMIN">SUPER_ADMIN</option>
          </select>
        </label>
        <label>
          <span>상태</span>
          <select name="status">
            <option value="">전체</option>
            <option value="ACTIVE">ACTIVE</option>
            <option value="BANNED">BANNED</option>
            <option value="DELETED">DELETED</option>
          </select>
        </label>
        <label>
          <span>시작일</span>
          <input type="date" name="from">
        </label>
        <label>
          <span>종료일</span>
          <input type="date" name="to">
        </label>
        <button type="submit">검색</button>
      </form>

      <div class="data-table-wrap">
        <table class="data-table member-table">
          <thead>
            <tr>
              <th scope="col">회원 ID</th>
              <th scope="col">이메일</th>
              <th scope="col">이름</th>
              <th scope="col">권한</th>
              <th scope="col">상태</th>
              <th scope="col">가입일</th>
              <th scope="col">관리</th>
            </tr>
          </thead>
          <tbody id="adminMemberTableBody">
            <tr class="data-table-empty">
              <td colspan="7">회원 목록을 불러오는 중입니다.</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="table-pagination" aria-label="회원 목록 페이지 이동">
        <button type="button" id="btnPrevMembers" class="pagination-btn">이전</button>
        <span id="memberPageInfo">-</span>
        <button type="button" id="btnNextMembers" class="pagination-btn">다음</button>
      </div>
    </section>

    <section class="admin-section" aria-label="신고 목록">
      <div class="section-header section-header-reports">
        <div>
          <h2>신고 관리</h2>
          <p id="adminReportCount">신고를 불러오는 중입니다.</p>
        </div>

        <div class="section-controls">
          <label class="page-size-control">
            <span>표시 개수</span>
            <select id="reportPageSize">
              <option value="5">5개</option>
              <option value="10">10개</option>
              <option value="20" selected>20개</option>
            </select>
          </label>

          <div class="report-filter" role="group" aria-label="신고 상태 필터">
            <button type="button" class="report-filter-btn is-active" data-status="ALL">전체</button>
            <button type="button" class="report-filter-btn" data-status="PENDING">대기</button>
            <button type="button" class="report-filter-btn" data-status="RESOLVED">처리완료</button>
          </div>
        </div>
      </div>

      <form id="reportSearchForm" class="report-search-form">
        <label>
          <span>상태</span>
          <select name="status">
            <option value="">전체</option>
            <option value="PENDING">대기</option>
            <option value="RESOLVED">처리완료</option>
          </select>
        </label>
        <label>
          <span>신고자</span>
          <input type="search" name="reporter" placeholder="이름 또는 이메일">
        </label>
        <label>
          <span>신고 대상</span>
          <input type="search" name="target" placeholder="이름 또는 이메일">
        </label>
        <label>
          <span>시작일</span>
          <input type="date" name="from">
        </label>
        <label>
          <span>종료일</span>
          <input type="date" name="to">
        </label>
        <button type="submit">검색</button>
      </form>

      <div class="data-table-wrap">
        <table class="data-table report-table">
          <thead>
            <tr>
              <th scope="col">신고 ID</th>
              <th scope="col">신고 대상</th>
              <th scope="col">신고자</th>
              <th scope="col">사유</th>
              <th scope="col">상태</th>
              <th scope="col">생성일</th>
              <th scope="col">관리</th>
            </tr>
          </thead>
          <tbody id="adminReportTableBody">
            <tr class="data-table-empty">
              <td colspan="7">신고 목록을 불러오는 중입니다.</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="table-pagination" aria-label="신고 목록 페이지 이동">
        <button type="button" id="btnPrevReports" class="pagination-btn">이전</button>
        <span id="reportPageInfo">-</span>
        <button type="button" id="btnNextReports" class="pagination-btn">다음</button>
      </div>
    </section>

    <section class="admin-section" aria-label="작업 로그">
      <div class="section-header">
        <div>
          <h2>작업 로그</h2>
          <p id="adminActionLogCount">작업 로그를 불러오는 중입니다.</p>
        </div>

        <label class="page-size-control">
          <span>표시 개수</span>
          <select id="actionLogPageSize">
            <option value="5">5개</option>
            <option value="10">10개</option>
            <option value="20" selected>20개</option>
          </select>
        </label>
      </div>

      <div class="data-table-wrap">
        <form id="actionLogSearchForm" class="action-log-search-form">
          <label>
            <span>작업 종류</span>
            <select name="action_type">
              <option value="">전체</option>
              <option value="MEMBER_BANNED">회원 정지</option>
              <option value="MEMBER_UNBANNED">회원 해제</option>
              <option value="REPORT_RESOLVED">신고 처리</option>
              <option value="MEMBER_PROMOTED_TO_ADMIN">관리자 승격</option>
              <option value="MEMBER_DEMOTED_TO_USER">권한 회수</option>
            </select>
          </label>
          <label>
            <span>수행 관리자</span>
            <input type="search" name="admin" placeholder="이름 또는 이메일">
          </label>
          <label>
            <span>시작일</span>
            <input type="date" name="from">
          </label>
          <label>
            <span>종료일</span>
            <input type="date" name="to">
          </label>
          <button type="submit">검색</button>
        </form>

        <table class="data-table action-log-table">
          <thead>
            <tr>
              <th scope="col">로그 ID</th>
              <th scope="col">수행 관리자</th>
              <th scope="col">작업</th>
              <th scope="col">대상</th>
              <th scope="col">상세</th>
              <th scope="col">수행 시각</th>
            </tr>
          </thead>
          <tbody id="adminActionLogTableBody">
            <tr class="data-table-empty">
              <td colspan="6">작업 로그를 불러오는 중입니다.</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="table-pagination" aria-label="작업 로그 페이지 이동">
        <button type="button" id="btnPrevActionLogs" class="pagination-btn">이전</button>
        <span id="actionLogPageInfo">-</span>
        <button type="button" id="btnNextActionLogs" class="pagination-btn">다음</button>
      </div>
    </section>
  </main>

  <div class="report-resolution-modal" id="reportResolutionModal" aria-hidden="true">
    <div class="report-resolution-backdrop" data-close-resolution-modal></div>
    <section class="report-resolution-dialog" role="dialog" aria-modal="true" aria-labelledby="reportResolutionTitle">
      <div class="report-resolution-header">
        <h2 id="reportResolutionTitle">신고 처리</h2>
        <button type="button" class="report-resolution-close" data-close-resolution-modal aria-label="닫기">x</button>
      </div>

      <form id="reportResolutionForm" class="report-resolution-form">
        <div class="report-resolution-summary" id="reportResolutionSummary">
          <div>
            <span>신고 대상</span>
            <strong id="resolutionTarget">-</strong>
          </div>
          <div>
            <span>신고자</span>
            <strong id="resolutionReporter">-</strong>
          </div>
          <div class="report-resolution-reason">
            <span>신고 사유</span>
            <strong id="resolutionReason">-</strong>
          </div>
        </div>

        <label>
          <span>처리 결과</span>
          <select name="resolution_type" required>
            <option value="">선택</option>
            <option value="ACCEPTED">신고 인정</option>
            <option value="REJECTED">신고 반려</option>
            <option value="NO_ACTION">조치 없음</option>
          </select>
        </label>

        <label>
          <span>신고자 안내 문구</span>
          <textarea
            name="resolution_message"
            rows="5"
            maxlength="500"
            placeholder="처리 결과와 안내 내용을 입력하세요."
            required></textarea>
        </label>

        <div class="report-resolution-actions">
          <button type="button" class="btn-secondary" data-close-resolution-modal>취소</button>
          <button type="submit" class="btn-primary">처리 완료</button>
        </div>
      </form>
    </section>
  </div>

  <script>
    window.contextPath = "${pageContext.request.contextPath}";
  </script>
  <script type="module" src="${pageContext.request.contextPath}/resources/js/admin/adminGuard.js"></script>
  <script type="module" src="${pageContext.request.contextPath}/resources/js/admin/dashboardSummary.js"></script>
  <script type="module" src="${pageContext.request.contextPath}/resources/js/admin/memberList.js"></script>
  <script type="module" src="${pageContext.request.contextPath}/resources/js/admin/reportList.js"></script>
  <script type="module" src="${pageContext.request.contextPath}/resources/js/admin/actionLogList.js"></script>
  <script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>
</body>
</html>

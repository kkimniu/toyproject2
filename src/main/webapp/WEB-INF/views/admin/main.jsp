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
      </div>

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
    </section>

    <section class="admin-section" aria-label="신고 목록">
      <div class="section-header section-header-reports">
        <div>
          <h2>신고 관리</h2>
          <p id="adminReportCount">신고를 불러오는 중입니다.</p>
        </div>

        <div class="report-filter" role="group" aria-label="신고 상태 필터">
          <button type="button" class="report-filter-btn is-active" data-status="ALL">전체</button>
          <button type="button" class="report-filter-btn" data-status="PENDING">대기</button>
          <button type="button" class="report-filter-btn" data-status="RESOLVED">처리완료</button>
        </div>
      </div>

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
  <script type="module" src="${pageContext.request.contextPath}/resources/js/admin/memberList.js"></script>
  <script type="module" src="${pageContext.request.contextPath}/resources/js/admin/reportList.js"></script>
  <script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>
</body>
</html>

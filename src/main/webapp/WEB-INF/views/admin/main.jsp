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

  <script>
    window.contextPath = "${pageContext.request.contextPath}";
  </script>
  <script type="module" src="${pageContext.request.contextPath}/resources/js/admin/adminGuard.js"></script>
  <script type="module" src="${pageContext.request.contextPath}/resources/js/admin/memberList.js"></script>
  <script type="module" src="${pageContext.request.contextPath}/resources/js/admin/reportList.js"></script>
  <script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>
</body>
</html>

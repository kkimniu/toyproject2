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
      <h1>회원 관리</h1>
    </section>

    <section class="member-summary" aria-label="회원 목록 요약">
      <span id="adminMemberCount">회원을 불러오는 중입니다.</span>
    </section>

    <section class="member-table-section" aria-label="회원 목록">
      <div class="member-table-wrap">
        <table class="member-table">
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
            <tr class="member-table-empty">
              <td colspan="7">회원 목록을 불러오는 중입니다.</td>
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
  <script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>
</body>
</html>

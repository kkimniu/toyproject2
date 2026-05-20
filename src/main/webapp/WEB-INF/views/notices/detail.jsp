<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>공지사항 | RoomMate</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/notice/notice.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<main class="notice-page">
  <a class="notice-back" href="${pageContext.request.contextPath}/notices">목록</a>
  <article id="noticeDetail" class="notice-detail">
    <p class="notice-empty">공지사항을 불러오는 중입니다.</p>
  </article>
</main>

<script>
  window.contextPath = "${pageContext.request.contextPath}";
  window.noticeId = "${noticeId}";
</script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/notice/noticeDetail.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>
</body>
</html>

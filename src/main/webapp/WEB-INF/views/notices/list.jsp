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
  <section class="notice-head">
    <h1>공지사항</h1>
    <form id="noticeSearchForm" class="notice-search">
      <input type="search" name="keyword" placeholder="공지 검색">
      <button type="submit">검색</button>
    </form>
  </section>

  <section id="noticeList" class="notice-list">
    <p class="notice-empty">공지사항을 불러오는 중입니다.</p>
  </section>

  <div class="notice-pagination">
    <button type="button" id="btnPrevNotices">이전</button>
    <span id="noticePageInfo">-</span>
    <button type="button" id="btnNextNotices">다음</button>
  </div>
</main>

<script>
  window.contextPath = "${pageContext.request.contextPath}";
</script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/notice/noticeList.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>
</body>
</html>

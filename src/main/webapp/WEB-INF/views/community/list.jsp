<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>커뮤니티 | RoomMate</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/community/community.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<main class="community-page">
  <section class="community-head">
    <div>
      <p>커뮤니티</p>
      <h1>룸메이트 생활 이야기</h1>
    </div>
    <a class="community-primary-link" href="${pageContext.request.contextPath}/community/new">글쓰기</a>
  </section>

  <form id="communitySearchForm" class="community-search">
    <input type="search" name="keyword" placeholder="제목 또는 내용 검색">
    <button type="submit">검색</button>
  </form>

  <section id="communityPostList" class="community-list">
    <p class="community-empty">게시글을 불러오는 중입니다.</p>
  </section>

  <div class="community-pagination">
    <button type="button" id="btnPrevCommunityPosts">이전</button>
    <span id="communityPostPageInfo">-</span>
    <button type="button" id="btnNextCommunityPosts">다음</button>
  </div>
</main>

<script>
  window.contextPath = "${pageContext.request.contextPath}";
</script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/community/communityList.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>
</body>
</html>

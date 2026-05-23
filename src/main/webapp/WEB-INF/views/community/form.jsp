<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>커뮤니티 글쓰기 | RoomMate</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/community/community.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<main class="community-page">
  <section class="community-form-head">
    <p>커뮤니티</p>
    <h1 id="communityFormTitle">게시글 작성</h1>
  </section>

  <form id="communityPostForm" class="community-form">
    <label>
      <span>제목</span>
      <input type="text" name="title" maxlength="150" required>
    </label>
    <label>
      <span>내용</span>
      <textarea name="content" rows="12" required></textarea>
    </label>
    <div class="community-form-actions">
      <a class="community-secondary-link" href="${pageContext.request.contextPath}/community">취소</a>
      <button type="submit" class="community-primary-btn">저장</button>
    </div>
  </form>
</main>

<script>
  window.contextPath = "${pageContext.request.contextPath}";
  window.communityPostId = "${communityPostId}";
</script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/community/communityForm.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>
</body>
</html>

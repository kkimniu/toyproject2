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
  <article id="communityPostDetail" class="community-detail">
    <p class="community-empty">게시글을 불러오는 중입니다.</p>
  </article>
  <div class="community-detail-actions">
    <a class="community-secondary-link" href="${pageContext.request.contextPath}/community">목록</a>
    <button type="button" id="btnReportCommunityPost" class="community-secondary-link" style="display:none;">신고</button>
    <a id="btnEditCommunityPost" class="community-secondary-link" href="#" style="display:none;">수정</a>
    <button type="button" id="btnDeleteCommunityPost" class="community-danger-btn" style="display:none;">삭제</button>
  </div>

  <section class="community-comments" aria-label="댓글">
    <div class="community-comments-head">
      <h2>댓글</h2>
      <span id="communityCommentCount">0개</span>
    </div>
    <form id="communityCommentForm" class="community-comment-form">
      <textarea name="content" rows="3" maxlength="1000" placeholder="댓글을 입력하세요." required></textarea>
      <button type="submit" class="community-primary-btn">등록</button>
    </form>
    <div id="communityCommentList" class="community-comment-list">
      <p class="community-empty">댓글을 불러오는 중입니다.</p>
    </div>
  </section>
</main>

<script>
  window.contextPath = "${pageContext.request.contextPath}";
  window.communityPostId = "${communityPostId}";
</script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/community/communityDetail.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>
</body>
</html>

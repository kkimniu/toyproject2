<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>채팅 | 룸메이트</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/chat/chat-room.css"/>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<main class="chat-page chat-list-page">
  <section class="chat-card">
    <div class="chat-header">
      <h1 class="chat-title">채팅</h1>
      <button type="button" id="btnRefreshChats" class="btn-link">새로고침</button>
    </div>

    <div id="chatRoomList" class="chat-room-list">
      <div class="chat-empty">채팅방을 불러오는 중...</div>
    </div>
  </section>
</main>

<script type="module" src="${pageContext.request.contextPath}/resources/js/chat/chatList.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>
</body>
</html>

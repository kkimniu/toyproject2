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

<main class="chat-page">
  <div id="chat-room-data" data-chat-room-id="${chatRoomId}"></div>

  <section class="chat-card">
    <div class="chat-header">
      <button type="button" id="btnBack" class="btn-link">목록</button>
      <h1 class="chat-title">채팅</h1>
      <button type="button" id="btnLeaveChat" class="btn-link">나가기</button>
    </div>

    <div id="chatList" class="chat-list" aria-live="polite">
      <div class="chat-empty">메시지를 불러오는 중...</div>
    </div>

    <form id="chatForm" class="chat-input-bar" autocomplete="off">
      <input id="chatInput" class="chat-input" type="text" placeholder="메시지를 입력하세요" maxlength="500"/>
      <button id="btnSend" class="chat-send" type="submit">전송</button>
    </form>
  </section>
</main>

<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/chat/chatRoom.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>
</body>
</html>

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
      <button type="button" id="btnReportChat" class="btn-link">신고</button>
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

  <div class="chat-report-modal-overlay" id="chatReportModal" aria-hidden="true">
    <div class="chat-report-modal" role="dialog" aria-modal="true" aria-labelledby="chatReportTitle">
      <div class="chat-report-modal-header">
        <h3 id="chatReportTitle">채팅 신고</h3>
        <button type="button" class="chat-report-modal-close" data-close-chat-report aria-label="닫기">x</button>
      </div>
      <form id="chatReportForm">
        <label for="chatReportReason">신고 사유</label>
        <textarea id="chatReportReason" name="reason" rows="5" maxlength="1000" placeholder="채팅에서 문제가 된 내용을 구체적으로 입력해주세요."></textarea>
        <p class="chat-report-message" id="chatReportMessage"></p>
        <div class="chat-report-modal-actions">
          <button type="button" class="btn-link chat-report-cancel" data-close-chat-report>취소</button>
          <button type="submit" class="chat-report-submit">접수</button>
        </div>
      </form>
    </div>
  </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/chat/chatRoom.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>
</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>채팅 | RoomMate</title>
  <style>
    body { margin: 0; background: #f7f7f8; font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; color: #111827; }
    .chat-shell { max-width: 880px; margin: 48px auto; padding: 0 20px; }
    .chat-panel { height: 640px; border: 1px solid #e5e7eb; border-radius: 8px; background: #fff; box-shadow: 0 18px 42px rgba(16, 24, 40, .08); overflow: hidden; display: flex; flex-direction: column; }
    .chat-header { padding: 18px 22px; border-bottom: 1px solid #eef2f6; font-weight: 800; }
    .chat-messages { flex: 1; padding: 20px 22px; overflow-y: auto; background: #f9fafb; display: flex; flex-direction: column; gap: 12px; }
    .chat-empty { color: #667085; text-align: center; margin: auto; }
    .message-row { display: flex; }
    .message-row.mine { justify-content: flex-end; }
    .message-bubble { max-width: min(560px, 76%); border: 1px solid #e5e7eb; border-radius: 8px; padding: 10px 12px; background: #fff; }
    .message-row.mine .message-bubble { background: #111827; color: #fff; border-color: #111827; }
    .message-meta { font-size: 12px; color: #667085; margin-bottom: 4px; }
    .message-row.mine .message-meta { color: #d1d5db; }
    .message-text { white-space: pre-wrap; word-break: break-word; line-height: 1.45; }
    .chat-form { display: flex; gap: 10px; padding: 14px; border-top: 1px solid #eef2f6; background: #fff; }
    .chat-form textarea { flex: 1; min-height: 44px; max-height: 120px; resize: vertical; border: 1px solid #d0d5dd; border-radius: 8px; padding: 10px 12px; font: inherit; }
    .chat-form button { width: 88px; border: 0; border-radius: 8px; background: #111827; color: #fff; font-weight: 700; cursor: pointer; }
    .chat-form button:disabled { opacity: .5; cursor: not-allowed; }
  </style>
</head>
<body>
  <jsp:include page="/WEB-INF/views/common/header.jsp"/>
  <main class="chat-shell">
    <section class="chat-panel">
      <div class="chat-header">채팅방 #${chatRoomId}</div>
      <div class="chat-messages" id="chatMessages">
        <div class="chat-empty">메시지를 불러오는 중입니다.</div>
      </div>
      <form class="chat-form" id="chatMessageForm">
        <textarea id="chatMessageInput" placeholder="메시지를 입력하세요" maxlength="1000"></textarea>
        <button type="submit" id="chatSendButton">전송</button>
      </form>
    </section>
  </main>
  <script>
    window.chatRoomId = ${chatRoomId};
  </script>
  <script type="module" src="${pageContext.request.contextPath}/resources/js/chat/room.js"></script>
</body>
</html>

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
    .chat-list-shell { max-width: 880px; margin: 48px auto; padding: 0 20px; }
    .chat-list-panel { border: 1px solid #e5e7eb; border-radius: 8px; background: #fff; box-shadow: 0 18px 42px rgba(16, 24, 40, .08); overflow: hidden; }
    .chat-list-header { padding: 18px 22px; border-bottom: 1px solid #eef2f6; font-weight: 800; }
    .chat-list-body { padding: 0; color: #667085; }
    .chat-empty { padding: 28px 22px; }
    .chat-room-item { display: flex; gap: 14px; align-items: center; width: 100%; padding: 16px 22px; border: 0; border-bottom: 1px solid #eef2f6; background: #fff; text-align: left; cursor: pointer; }
    .chat-room-item:hover { background: #f9fafb; }
    .chat-room-avatar { width: 48px; height: 48px; border-radius: 50%; object-fit: cover; background: #f3f4f6; flex: 0 0 48px; }
    .chat-room-main { min-width: 0; flex: 1; }
    .chat-room-top { display: flex; justify-content: space-between; gap: 12px; margin-bottom: 4px; }
    .chat-room-name { font-weight: 800; color: #111827; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
    .chat-room-time { font-size: 12px; color: #98a2b3; white-space: nowrap; }
    .chat-room-title { font-size: 13px; color: #4b5563; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
    .chat-room-message { margin-top: 4px; font-size: 13px; color: #667085; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
  </style>
</head>
<body>
  <jsp:include page="/WEB-INF/views/common/header.jsp"/>
  <main class="chat-list-shell">
    <section class="chat-list-panel">
      <div class="chat-list-header">채팅</div>
      <div class="chat-list-body" id="chatRoomList">
        <div class="chat-empty">채팅 목록을 불러오는 중입니다.</div>
      </div>
    </section>
  </main>
  <script type="module" src="${pageContext.request.contextPath}/resources/js/chat/list.js"></script>
</body>
</html>

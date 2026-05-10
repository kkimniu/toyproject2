import { requireLogin } from "../common/authGuard.js";
import { apiRequest } from "../common/apiClient.js";
import { getAccessToken, getTokenType } from "../common/authTokenStorage.js";

let stompClient = null;
let reconnectTimer = null;

function getAccessTokenFallback() {
  return getAccessToken();
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function formatTime(value) {
  if (!value) return "";
  // sent_at: "2026-01-17T10:00:00" 형태를 예상
  let parts = null;
  if (Array.isArray(value)) {
    parts = value;
  } else {
    const s = String(value);
    if (s.includes("T")) {
      parts = s
        .replace("T", "-")
        .replaceAll(":", "-")
        .split("-")
        .map(Number);
    } else if (/^\d{4},\d{1,2},\d{1,2},\d{1,2},\d{1,2}/.test(s)) {
      parts = s.split(",").map(Number);
    }
  }

  if (parts) {
    const [year, month, day, hour = 0, minute = 0, second = 0] = parts;
    return `${year}년 ${month}월 ${day}일 ${hour}시 ${minute}분 ${second}초`;
  }

  return String(value);
}

// 서버가 me 여부를 주지 않으므로, 일단 sender_id만 보여주고,
// 나중에 WebSocket 붙이면서 "내 memberId"를 같이 내려받거나 전역에서 주입해서 me/other 구분하면 됨.
function renderMessageItem(msg, myMemberId) {
  const senderId = msg.sender_id ?? msg.senderId;
  const text = msg.message;
  const sentAt = msg.sent_at ?? msg.sentAt;

  const isMe = myMemberId != null && Number(senderId) === Number(myMemberId);
  const rowClass = isMe ? "me" : "other";
  const bubbleClass = isMe ? "me" : "other";

  return `
    <div class="chat-row ${rowClass}">
      <div>
        <div class="chat-bubble ${bubbleClass}">${escapeHtml(text)}</div>
        <div class="chat-meta">${escapeHtml(formatTime(sentAt))}</div>
      </div>
    </div>
  `;
}

function scrollToBottom(el) {
  el.scrollTop = el.scrollHeight;
}

function pick(obj, keys) {
  for (const k of keys) {
    if (obj && obj[k] != null) return obj[k];
  }
  return undefined;
}

async function fetchMyMemberId() {
  const res = await apiRequest("/api/members/me", { method: "GET" });

  if (!res.ok) {
    return null;
  }

  const me = await res.json();
  const myId = pick(me, ["memberId", "member_id", "id"]);
  return myId != null ? Number(myId) : null;
}

function appendMessage(listEl, msg, myMemberId) {
  listEl.insertAdjacentHTML("beforeend", renderMessageItem(msg, myMemberId));
  listEl.scrollTop = listEl.scrollHeight;
}

async function loadMessages(chatRoomId, myMemberId) {
  const listEl = document.getElementById("chatList");
  if (!listEl) return;

  const res = await apiRequest(`/api/chat/rooms/${encodeURIComponent(chatRoomId)}/messages`, {
    method: "GET",
  });

  if (!res.ok) {
    listEl.innerHTML = `<div class="chat-empty">메시지를 불러오지 못했습니다.</div>`;
    return;
  }

  const messages = await res.json();
  if (!Array.isArray(messages) || messages.length === 0) {
    listEl.innerHTML = `<div class="chat-empty">아직 메시지가 없습니다. 첫 메시지를 보내보세요.</div>`;
    return;
  }

  listEl.innerHTML = messages.map((m) => renderMessageItem(m, myMemberId)).join("");
  scrollToBottom(listEl);
  await markRead(chatRoomId);
}

async function markRead(chatRoomId) {
  try {
    await apiRequest(`/api/chat/rooms/${encodeURIComponent(chatRoomId)}/read`, {
      method: "PATCH",
    });
  } catch (e) {
    console.warn("[chat] mark read failed:", e);
  }
}

function scheduleReconnect(chatRoomId, myMemberId) {
  if (reconnectTimer) return;

  reconnectTimer = setTimeout(() => {
    reconnectTimer = null;
    if (stompClient) {
      try {
        if (stompClient.connected) {
          stompClient.disconnect();
        }
      } catch (e) {
        console.warn("[chat] reconnect disconnect error:", e);
      } finally {
        stompClient = null;
      }
    }
    connectStomp(chatRoomId, myMemberId);
  }, 3000);
}

function connectStomp(chatRoomId, myMemberId) {
  const listEl = document.getElementById("chatList");
  if (!listEl) return null;

  const token = getAccessTokenFallback();
  if (!token) {
    console.warn("[chat] access token이 없습니다.");
    return null;
  }

  const socket = new SockJS("/ws");
  const client = Stomp.over(socket);
  client.debug = null;

  const headers = {
    Authorization: `${getTokenType()} ${token}`,
   };

  client.connect(
    headers,
    () => {
      console.log("[chat] stomp connected");
      // 구독: /topic/chat/{chatRoomId}
      reconnectTimer = null;
      stompClient = client;

      client.subscribe(`/topic/chat/${chatRoomId}`, (frame) => {
        try {
          const body = JSON.parse(frame.body);
          appendMessage(listEl, body, myMemberId);
          markRead(chatRoomId);
        } catch (e) {
          console.warn("invalid message frame:", e);
        }
      });
    },
    (err) => {
      console.warn("[chat] stomp connect error:", err);
      scheduleReconnect(chatRoomId, myMemberId);
    }
  );
  return client;
}

document.addEventListener("DOMContentLoaded", async () => {
  if (!requireLogin()) return;

  const root = document.getElementById("chat-room-data");
  const chatRoomId = root?.dataset?.chatRoomId;

  if (!chatRoomId) {
    console.warn("chat-room-data의 data-chat-room-id가 없습니다.");
    return;
  }

  // 뒤로가기
  const btnBack = document.getElementById("btnBack");
  if (btnBack) {
    btnBack.addEventListener("click", () => {
      location.href = "/chats";
    });
  }

  const btnLeaveChat = document.getElementById("btnLeaveChat");
  if (btnLeaveChat) {
    btnLeaveChat.addEventListener("click", async () => {
      if (!confirm("이 방에서 나갈까요?")) return;

      const res = await apiRequest(`/api/chat/rooms/${encodeURIComponent(chatRoomId)}/me`, {
        method: "DELETE",
      });

      if (!res.ok) {
        alert("채팅방 나가기에 실패했습니다.");
        return;
      }

      location.href = "/chats";
    });
  }

  const myMemberId = await fetchMyMemberId();
  if (myMemberId == null) {
    alert("로그인 정보가 만료되었습니다. 다시 로그인해주세요.");
    window.openAuthModal?.("login");
    return;
  }
  await loadMessages(chatRoomId, myMemberId);

  connectStomp(chatRoomId, myMemberId);

  const form = document.getElementById("chatForm");
  const input = document.getElementById("chatInput");

  if (form && input) {
    form.addEventListener("submit", (e) => {
      e.preventDefault();

      const text = String(input.value ?? "").trim();
      if (!text) return;

      if (!stompClient || !stompClient.connected) {
        alert("채팅 서버에 연결되지 않았습니다.");
        return;
      }

      // 전송: /app/chat/{chatRoomId}
      stompClient.send(
        `/app/chat/${chatRoomId}`,
        {},
        JSON.stringify({ message: text })
      );

      input.value = "";
      input.focus();
    });
  }
});

window.addEventListener("beforeunload", () => {
  if (!stompClient) return;

  try {
    if (stompClient.connected) {
      stompClient.disconnect();
    }
  } catch (e) {
    console.warn("[chat] disconnect error:", e);
  } finally {
    stompClient = null;
  }
});

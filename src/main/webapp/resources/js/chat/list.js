import { apiRequest } from "../common/apiClient.js";
import { requireLogin } from "../common/authGuard.js";

const listEl = document.getElementById("chatRoomList");
const defaultProfileImage = "/resources/img/default-profile.svg";

document.addEventListener("DOMContentLoaded", async () => {
  if (!requireLogin()) return;
  await loadChatRooms();
});

async function loadChatRooms() {
  if (!listEl) return;

  try {
    const response = await apiRequest("/api/chat/rooms", {
      method: "GET",
      headers: { Accept: "application/json" },
    });

    if (!response.ok) throw new Error(`chat rooms api failed: ${response.status}`);

    renderChatRooms(await response.json());
  } catch (error) {
    console.error(error);
    listEl.innerHTML = '<div class="chat-empty">채팅 목록을 불러오지 못했습니다.</div>';
  }
}

function renderChatRooms(rooms) {
  if (!Array.isArray(rooms) || rooms.length === 0) {
    listEl.innerHTML = '<div class="chat-empty">아직 채팅 기록이 없습니다.</div>';
    return;
  }

  listEl.innerHTML = rooms.map(renderChatRoom).join("");
  listEl.querySelectorAll(".chat-room-item").forEach((item) => {
    item.addEventListener("click", () => {
      window.location.href = `/chat/rooms/${encodeURIComponent(item.dataset.chatRoomId)}`;
    });
  });
}

function renderChatRoom(room) {
  const chatRoomId = room.chat_room_id ?? room.chatRoomId;
  const partnerName = room.partner_name ?? room.partnerName ?? "회원";
  const photoUrl = room.partner_photo_url ?? room.partnerPhotoUrl ?? defaultProfileImage;
  const roomTitle = room.room_title ?? room.roomTitle ?? "방 정보 없음";
  const lastMessage = room.last_message ?? room.lastMessage ?? "아직 메시지가 없습니다.";
  const lastMessageAt = room.last_message_at ?? room.lastMessageAt;

  return `
    <button type="button" class="chat-room-item" data-chat-room-id="${escapeAttribute(chatRoomId)}">
      <img class="chat-room-avatar" src="${escapeAttribute(photoUrl || defaultProfileImage)}" alt="${escapeAttribute(partnerName)} 프로필" onerror="this.src='${defaultProfileImage}'">
      <div class="chat-room-main">
        <div class="chat-room-top">
          <div class="chat-room-name">${escapeHtml(partnerName)}</div>
          <div class="chat-room-time">${escapeHtml(formatTime(lastMessageAt))}</div>
        </div>
        <div class="chat-room-title">${escapeHtml(roomTitle)}</div>
        <div class="chat-room-message">${escapeHtml(lastMessage)}</div>
      </div>
    </button>
  `;
}

function formatTime(value) {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "";
  return date.toLocaleString("ko-KR", {
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function escapeAttribute(value) {
  return escapeHtml(value);
}

import { requireLogin } from "../common/authGuard.js";
import { apiRequest } from "../common/apiClient.js";

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

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return String(value);
  return date.toLocaleString("ko-KR", {
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function pick(room, keys) {
  for (const key of keys) {
    if (room && room[key] != null) return room[key];
  }
  return undefined;
}

function renderRoom(room) {
  const chatRoomId = pick(room, ["chatRoomId", "chat_room_id"]);
  const roomTitle = pick(room, ["roomTitle", "room_title"]) ?? "게시글";
  const otherUnavailable = isUnavailableMember(room);
  const otherName = otherUnavailable ? unavailableMemberText(room) : (pick(room, ["otherName", "other_name"]) ?? "상대방");
  const lastMessage = pick(room, ["lastMessage", "last_message"]) ?? "아직 메시지가 없습니다.";
  const lastSentAt = pick(room, ["lastSentAt", "last_sent_at"]);
  const unreadCount = Number(pick(room, ["unreadCount", "unread_count"]) ?? 0);
  const thumbnailUrl = pick(room, ["roomThumbnailUrl", "room_thumbnail_url"]);
  const otherPhotoUrl = pick(room, ["otherPhotoUrl", "other_photo_url"]);
  const imageUrl = thumbnailUrl || otherPhotoUrl || "/resources/img/default-room.svg";

  return `
    <a class="chat-room-item ${otherUnavailable ? "is-deleted-member" : ""}" href="/chats/${encodeURIComponent(chatRoomId)}">
      <img class="chat-room-thumb" src="${escapeHtml(imageUrl)}" alt="">
      <div class="chat-room-main">
        <div class="chat-room-top">
          <strong class="chat-room-name">${escapeHtml(otherName)}</strong>
          ${otherUnavailable ? `<span class="chat-room-member-state">${escapeHtml(unavailableMemberBadge(room))}</span>` : ""}
          <span class="chat-room-time">${escapeHtml(formatTime(lastSentAt))}</span>
        </div>
        <div class="chat-room-title">${escapeHtml(roomTitle)}</div>
        <div class="chat-room-preview">${escapeHtml(lastMessage)}</div>
      </div>
      ${unreadCount > 0 ? `<span class="chat-room-unread">${unreadCount > 99 ? "99+" : unreadCount}</span>` : ""}
    </a>
  `;
}

function isUnavailableMember(room) {
  const deleted = pick(room, ["otherDeleted", "other_deleted"]);
  const status = pick(room, ["otherStatus", "other_status"]);
  return deleted === true || deleted === 1 || deleted === "1" || status === "DELETED" || status === "BANNED";
}

function unavailableMemberText(room) {
  return pick(room, ["otherStatus", "other_status"]) === "BANNED" ? "정지된 회원" : "탈퇴한 회원";
}

function unavailableMemberBadge(room) {
  return pick(room, ["otherStatus", "other_status"]) === "BANNED" ? "정지" : "탈퇴";
}

async function loadChatRooms() {
  const listEl = document.getElementById("chatRoomList");
  if (!listEl) return;

  listEl.innerHTML = `<div class="chat-empty">채팅방을 불러오는 중...</div>`;

  const res = await apiRequest("/api/chat/rooms", { method: "GET" });
  if (!res.ok) {
    listEl.innerHTML = `<div class="chat-empty">채팅방을 불러오지 못했습니다.</div>`;
    return;
  }

  const rooms = await res.json();
  if (!Array.isArray(rooms) || rooms.length === 0) {
    listEl.innerHTML = `<div class="chat-empty">아직 채팅방이 없습니다.</div>`;
    return;
  }

  listEl.innerHTML = rooms.map(renderRoom).join("");
}

document.addEventListener("DOMContentLoaded", () => {
  if (!requireLogin()) return;

  loadChatRooms();

  const refreshBtn = document.getElementById("btnRefreshChats");
  if (refreshBtn) {
    refreshBtn.addEventListener("click", loadChatRooms);
  }
});

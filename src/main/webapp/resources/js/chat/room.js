import { apiRequest } from "../common/apiClient.js";
import { requireLogin } from "../common/authGuard.js";

const chatRoomId = window.chatRoomId;
const messagesEl = document.getElementById("chatMessages");
const form = document.getElementById("chatMessageForm");
const input = document.getElementById("chatMessageInput");
const sendButton = document.getElementById("chatSendButton");

document.addEventListener("DOMContentLoaded", async () => {
  if (!requireLogin()) return;

  await loadMessages();

  form?.addEventListener("submit", async (event) => {
    event.preventDefault();
    await sendMessage();
  });

  input?.addEventListener("keydown", async (event) => {
    if (event.key === "Enter" && !event.shiftKey) {
      event.preventDefault();
      await sendMessage();
    }
  });
});

async function loadMessages() {
  if (!messagesEl || !chatRoomId) return;

  try {
    const response = await apiRequest(`/api/chat/rooms/${encodeURIComponent(chatRoomId)}/messages`, {
      method: "GET",
      headers: { Accept: "application/json" },
    });

    if (!response.ok) {
      throw new Error(`messages api failed: ${response.status}`);
    }

    renderMessages(await response.json());
  } catch (error) {
    console.error(error);
    messagesEl.innerHTML = '<div class="chat-empty">메시지를 불러오지 못했습니다.</div>';
  }
}

async function sendMessage() {
  const message = input?.value.trim();
  if (!message || !chatRoomId || !sendButton) return;

  sendButton.disabled = true;
  try {
    const response = await apiRequest(`/api/chat/rooms/${encodeURIComponent(chatRoomId)}/messages`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
      },
      body: JSON.stringify({ message }),
    });

    if (!response.ok) {
      throw new Error(`send message api failed: ${response.status}`);
    }

    input.value = "";
    await loadMessages();
  } catch (error) {
    console.error(error);
    alert("메시지를 보내지 못했습니다.");
  } finally {
    sendButton.disabled = false;
    input?.focus();
  }
}

function renderMessages(messages) {
  if (!messagesEl) return;
  if (!Array.isArray(messages) || messages.length === 0) {
    messagesEl.innerHTML = '<div class="chat-empty">아직 메시지가 없습니다.</div>';
    return;
  }

  messagesEl.innerHTML = messages.map(renderMessage).join("");
  messagesEl.scrollTop = messagesEl.scrollHeight;
}

function renderMessage(message) {
  const mine = Boolean(message.mine);
  const senderName = message.sender_name ?? message.senderName ?? "회원";
  const sentAt = message.sent_at ?? message.sentAt;
  const timeText = sentAt ? formatTime(sentAt) : "";

  return `
    <div class="message-row${mine ? " mine" : ""}">
      <div class="message-bubble">
        <div class="message-meta">${escapeHtml(senderName)}${timeText ? ` · ${escapeHtml(timeText)}` : ""}</div>
        <div class="message-text">${escapeHtml(message.message || "")}</div>
      </div>
    </div>
  `;
}

function formatTime(value) {
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

import { apiRequest } from "../common/apiClient.js";

const contextPath = window.contextPath || "";
const noticeId = window.noticeId;

document.addEventListener("DOMContentLoaded", loadNotice);

async function loadNotice() {
  const detail = document.getElementById("noticeDetail");
  if (!detail || !noticeId) return;
  const res = await apiRequest(`${contextPath}/api/notices/${encodeURIComponent(noticeId)}`, { method: "GET" });
  if (!res.ok) {
    detail.innerHTML = '<p class="notice-empty">공지사항을 찾을 수 없습니다.</p>';
    return;
  }
  const notice = await res.json();
  detail.innerHTML = `
    <header class="notice-detail-header">
      <h1>${notice.pinned ? '<span class="notice-pin">중요</span> ' : ""}${escapeHtml(notice.title)}</h1>
      <p class="notice-item-date">${escapeHtml(formatDate(notice.created_at))}</p>
    </header>
    <div class="notice-detail-content">${escapeHtml(notice.content)}</div>
  `;
}

function formatDate(value) {
  if (!value) return "-";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return String(value);
  return date.toLocaleString("ko-KR");
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

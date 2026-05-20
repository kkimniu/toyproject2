import { apiRequest } from "../common/apiClient.js";

const contextPath = window.contextPath || "";
let currentPage = 1;
let totalPages = 0;
let currentKeyword = "";

document.addEventListener("DOMContentLoaded", async () => {
  bindSearch();
  bindPagination();
  await loadNotices();
});

function bindSearch() {
  document.getElementById("noticeSearchForm")?.addEventListener("submit", async (event) => {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    currentKeyword = String(formData.get("keyword") || "").trim();
    currentPage = 1;
    await loadNotices();
  });
}

function bindPagination() {
  document.getElementById("btnPrevNotices")?.addEventListener("click", async () => {
    if (currentPage <= 1) return;
    currentPage -= 1;
    await loadNotices();
  });
  document.getElementById("btnNextNotices")?.addEventListener("click", async () => {
    if (currentPage >= totalPages) return;
    currentPage += 1;
    await loadNotices();
  });
}

async function loadNotices() {
  const list = document.getElementById("noticeList");
  if (!list) return;
  list.innerHTML = '<p class="notice-empty">공지사항을 불러오는 중입니다.</p>';
  const params = new URLSearchParams({ page: String(currentPage), size: "10" });
  if (currentKeyword) params.set("keyword", currentKeyword);
  const res = await apiRequest(`${contextPath}/api/notices?${params.toString()}`, { method: "GET" });
  if (!res.ok) {
    list.innerHTML = '<p class="notice-empty">공지사항을 불러오지 못했습니다.</p>';
    return;
  }
  const data = await res.json();
  const items = Array.isArray(data.items) ? data.items : [];
  currentPage = Number(data.page || 1);
  totalPages = Number(data.total_pages || 0);
  renderPagination();
  if (items.length === 0) {
    list.innerHTML = '<p class="notice-empty">등록된 공지사항이 없습니다.</p>';
    return;
  }
  list.innerHTML = items.map(renderNotice).join("");
}

function renderPagination() {
  const prev = document.getElementById("btnPrevNotices");
  const next = document.getElementById("btnNextNotices");
  const info = document.getElementById("noticePageInfo");
  if (!prev || !next || !info) return;
  prev.disabled = currentPage <= 1 || totalPages === 0;
  next.disabled = currentPage >= totalPages || totalPages === 0;
  info.textContent = totalPages === 0 ? "0 / 0" : `${currentPage} / ${totalPages}`;
}

function renderNotice(notice) {
  return `
    <a class="notice-item" href="${contextPath}/notices/${encodeURIComponent(notice.notice_id)}">
      <h2 class="notice-item-title">
        ${notice.pinned ? '<span class="notice-pin">중요</span>' : ""}
        ${escapeHtml(notice.title)}
      </h2>
      <p class="notice-item-date">${escapeHtml(formatDate(notice.created_at))}</p>
    </a>
  `;
}

function formatDate(value) {
  if (!value) return "-";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return String(value);
  return date.toLocaleDateString("ko-KR");
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

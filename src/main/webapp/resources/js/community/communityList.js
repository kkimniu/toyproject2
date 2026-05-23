import { apiRequest } from "../common/apiClient.js";

const contextPath = window.contextPath || "";
let currentPage = 1;
let totalPages = 0;
let currentKeyword = "";

document.addEventListener("DOMContentLoaded", async () => {
  bindSearch();
  bindPagination();
  await loadPosts();
});

function bindSearch() {
  document.getElementById("communitySearchForm")?.addEventListener("submit", async (event) => {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    currentKeyword = String(formData.get("keyword") || "").trim();
    currentPage = 1;
    await loadPosts();
  });
}

function bindPagination() {
  document.getElementById("btnPrevCommunityPosts")?.addEventListener("click", async () => {
    if (currentPage <= 1) return;
    currentPage -= 1;
    await loadPosts();
  });
  document.getElementById("btnNextCommunityPosts")?.addEventListener("click", async () => {
    if (currentPage >= totalPages) return;
    currentPage += 1;
    await loadPosts();
  });
}

async function loadPosts() {
  const list = document.getElementById("communityPostList");
  if (!list) return;
  list.innerHTML = '<p class="community-empty">게시글을 불러오는 중입니다.</p>';
  const params = new URLSearchParams({ page: String(currentPage), size: "10" });
  if (currentKeyword) params.set("keyword", currentKeyword);
  const res = await apiRequest(`${contextPath}/api/community/posts?${params.toString()}`, { method: "GET" });
  if (!res.ok) {
    list.innerHTML = '<p class="community-empty">게시글을 불러오지 못했습니다.</p>';
    return;
  }
  const data = await res.json();
  const items = Array.isArray(data.items) ? data.items : [];
  currentPage = Number(data.page || 1);
  totalPages = Number(data.total_pages || 0);
  renderPagination();
  if (items.length === 0) {
    list.innerHTML = '<p class="community-empty">등록된 게시글이 없습니다.</p>';
    return;
  }
  list.innerHTML = items.map(renderPost).join("");
}

function renderPost(post) {
  return `
    <a class="community-post-item" href="${contextPath}/community/${encodeURIComponent(post.community_post_id)}">
      <h2>${escapeHtml(post.title)}</h2>
      <p>${escapeHtml(post.content_preview || "")}</p>
      <div class="community-post-meta">
        <span>${escapeHtml(post.member_name || "알 수 없는 회원")}</span>
        <span>${escapeHtml(formatDate(post.created_at))}</span>
        <span>조회 ${Number(post.views || 0).toLocaleString("ko-KR")}</span>
      </div>
    </a>
  `;
}

function renderPagination() {
  const prev = document.getElementById("btnPrevCommunityPosts");
  const next = document.getElementById("btnNextCommunityPosts");
  const info = document.getElementById("communityPostPageInfo");
  if (!prev || !next || !info) return;
  prev.disabled = currentPage <= 1 || totalPages === 0;
  next.disabled = currentPage >= totalPages || totalPages === 0;
  info.textContent = totalPages === 0 ? "0 / 0" : `${currentPage} / ${totalPages}`;
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

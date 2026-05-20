import { apiRequest } from "../common/apiClient.js";

const contextPath = window.contextPath || "";
let currentPage = 1;
let totalPages = 0;
let currentFilters = {};

document.addEventListener("DOMContentLoaded", async () => {
  bindNoticeForm();
  bindNoticeSearch();
  bindNoticePagination();
  await loadNotices();
});

function bindNoticeForm() {
  const form = document.getElementById("noticeEditorForm");
  form?.addEventListener("submit", async (event) => {
    event.preventDefault();
    const formData = new FormData(form);
    const noticeId = String(formData.get("notice_id") || "").trim();
    const body = {
      title: String(formData.get("title") || "").trim(),
      content: String(formData.get("content") || "").trim(),
      pinned: formData.get("pinned") === "on",
      published: formData.get("published") === "on",
    };
    if (!body.title || !body.content) return;
    const url = noticeId
      ? `${contextPath}/api/admin/notices/${encodeURIComponent(noticeId)}`
      : `${contextPath}/api/admin/notices`;
    const res = await apiRequest(url, {
      method: noticeId ? "PATCH" : "POST",
      body: JSON.stringify(body),
    });
    if (!res.ok) {
      console.warn(`notice save failed: ${res.status}`);
      alert(`공지사항을 저장하지 못했습니다. (${res.status})`);
      return;
    }
    resetNoticeForm();
    await loadNotices(currentPage);
  });

  document.getElementById("btnResetNoticeForm")?.addEventListener("click", resetNoticeForm);
}

function bindNoticeSearch() {
  const form = document.getElementById("noticeSearchForm");
  form?.addEventListener("submit", async (event) => {
    event.preventDefault();
    const formData = new FormData(form);
    currentFilters = {};
    const keyword = String(formData.get("keyword") || "").trim();
    const published = String(formData.get("published") || "").trim();
    if (keyword) currentFilters.keyword = keyword;
    if (published) currentFilters.published = published;
    currentPage = 1;
    await loadNotices(1);
  });
}

function bindNoticePagination() {
  document.getElementById("btnPrevNotices")?.addEventListener("click", async () => {
    if (currentPage <= 1) return;
    await loadNotices(currentPage - 1);
  });
  document.getElementById("btnNextNotices")?.addEventListener("click", async () => {
    if (currentPage >= totalPages) return;
    await loadNotices(currentPage + 1);
  });
}

async function loadNotices(page = currentPage) {
  const tbody = document.getElementById("adminNoticeTableBody");
  const count = document.getElementById("adminNoticeCount");
  if (!tbody || !count) return;
  const params = new URLSearchParams({ page: String(page), size: "10", ...currentFilters });
  const res = await apiRequest(`${contextPath}/api/admin/notices?${params.toString()}`, { method: "GET" });
  if (!res.ok) {
    tbody.innerHTML = '<tr class="data-table-empty"><td colspan="5">공지사항을 불러오지 못했습니다.</td></tr>';
    return;
  }
  const data = await res.json();
  const items = Array.isArray(data.items) ? data.items : [];
  currentPage = Number(data.page || 1);
  totalPages = Number(data.total_pages || 0);
  count.textContent = `전체 공지사항 ${Number(data.total_count || 0).toLocaleString("ko-KR")}건`;
  renderPagination();
  if (items.length === 0) {
    tbody.innerHTML = '<tr class="data-table-empty"><td colspan="5">공지사항이 없습니다.</td></tr>';
    return;
  }
  tbody.innerHTML = items.map(renderNoticeRow).join("");
  bindNoticeRowActions(tbody);
}

function renderPagination() {
  const prev = document.getElementById("btnPrevNotices");
  const next = document.getElementById("btnNextNotices");
  const info = document.getElementById("noticeAdminPageInfo");
  if (!prev || !next || !info) return;
  prev.disabled = currentPage <= 1 || totalPages === 0;
  next.disabled = currentPage >= totalPages || totalPages === 0;
  info.textContent = totalPages === 0 ? "0 / 0" : `${currentPage} / ${totalPages}`;
}

function renderNoticeRow(notice) {
  return `
    <tr>
      <td>${escapeHtml(notice.notice_id)}</td>
      <td>${notice.pinned ? '<span class="notice-admin-pin">중요</span>' : ""}${escapeHtml(notice.title)}</td>
      <td>${notice.published ? "공개" : "비공개"}</td>
      <td>${escapeHtml(formatDate(notice.created_at))}</td>
      <td>
        <div class="member-actions">
          <button type="button" class="member-action-btn" data-edit-notice="${escapeHtml(notice.notice_id)}">수정</button>
          <button type="button" class="member-action-btn member-delete-btn" data-delete-notice="${escapeHtml(notice.notice_id)}">삭제</button>
        </div>
      </td>
    </tr>
  `;
}

function bindNoticeRowActions(container) {
  container.querySelectorAll("[data-edit-notice]").forEach((button) => {
    button.addEventListener("click", async () => {
      await loadNoticeToForm(button.dataset.editNotice);
    });
  });
  container.querySelectorAll("[data-delete-notice]").forEach((button) => {
    button.addEventListener("click", async () => {
      if (!confirm("공지사항을 삭제하시겠습니까?")) return;
      const res = await apiRequest(`${contextPath}/api/admin/notices/${encodeURIComponent(button.dataset.deleteNotice)}`, {
        method: "DELETE",
      });
      if (!res.ok) {
        alert("공지사항을 삭제하지 못했습니다.");
        return;
      }
      await loadNotices(currentPage);
    });
  });
}

async function loadNoticeToForm(noticeId) {
  const res = await apiRequest(`${contextPath}/api/admin/notices/${encodeURIComponent(noticeId)}`, { method: "GET" });
  if (!res.ok) return;
  const notice = await res.json();
  const form = document.getElementById("noticeEditorForm");
  if (!form) return;
  form.elements.notice_id.value = notice.notice_id || "";
  form.elements.title.value = notice.title || "";
  form.elements.content.value = notice.content || "";
  form.elements.pinned.checked = !!notice.pinned;
  form.elements.published.checked = !!notice.published;
  form.scrollIntoView({ behavior: "smooth", block: "start" });
}

function resetNoticeForm() {
  const form = document.getElementById("noticeEditorForm");
  form?.reset();
  if (form?.elements.notice_id) form.elements.notice_id.value = "";
  if (form?.elements.published) form.elements.published.checked = true;
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

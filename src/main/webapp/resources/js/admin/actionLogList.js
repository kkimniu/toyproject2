import { apiRequest } from "../common/apiClient.js";

const contextPath = window.contextPath || "";
let pageSize = 20;
let currentPage = 1;
let totalPages = 0;
let currentFilters = {};

document.addEventListener("DOMContentLoaded", async () => {
  bindSearchForm();
  bindPageSizeSelect();
  bindPaginationButtons();
  await loadActionLogs();
});

async function loadActionLogs(page = currentPage) {
  const count = document.getElementById("adminActionLogCount");
  const tableBody = document.getElementById("adminActionLogTableBody");
  if (!count || !tableBody) return;

  try {
    const params = new URLSearchParams({
      page: String(page),
      size: String(pageSize),
      ...currentFilters,
    });
    const response = await apiRequest(`${contextPath}/api/admin/action-logs?${params.toString()}`, {
      method: "GET",
    });

    if (!response.ok) {
      throw new Error(`admin action log api failed: ${response.status}`);
    }

    const data = await response.json();
    const logs = Array.isArray(data.items) ? data.items : [];
    currentPage = Number(data.page || 1);
    totalPages = Number(data.total_pages || 0);
    count.textContent = `전체 작업 로그 ${formatNumber(data.total_count || 0)}건`;
    renderPagination();

    if (logs.length === 0) {
      tableBody.innerHTML = `
        <tr class="data-table-empty">
          <td colspan="6">표시할 작업 로그가 없습니다.</td>
        </tr>
      `;
      return;
    }

    tableBody.innerHTML = logs.map(renderActionLogRow).join("");
  } catch (error) {
    console.error(error);
    count.textContent = "작업 로그를 불러오지 못했습니다.";
    tableBody.innerHTML = `
      <tr class="data-table-empty">
        <td colspan="6">작업 로그를 불러오지 못했습니다.</td>
      </tr>
    `;
    totalPages = 0;
    renderPagination();
  }
}

function bindSearchForm() {
  const form = document.getElementById("actionLogSearchForm");
  form?.addEventListener("submit", async (event) => {
    event.preventDefault();
    const formData = new FormData(form);
    currentFilters = Object.fromEntries(
      [...formData.entries()]
        .map(([key, value]) => [key, String(value).trim()])
        .filter(([, value]) => value)
    );
    currentPage = 1;
    await loadActionLogs(1);
  });
}

function bindPageSizeSelect() {
  const select = document.getElementById("actionLogPageSize");
  select?.addEventListener("change", async () => {
    pageSize = Number(select.value || 20);
    currentPage = 1;
    await loadActionLogs(1);
  });
}

function bindPaginationButtons() {
  document.getElementById("btnPrevActionLogs")?.addEventListener("click", async () => {
    if (currentPage <= 1) return;
    await loadActionLogs(currentPage - 1);
  });

  document.getElementById("btnNextActionLogs")?.addEventListener("click", async () => {
    if (currentPage >= totalPages) return;
    await loadActionLogs(currentPage + 1);
  });
}

function renderPagination() {
  const prevButton = document.getElementById("btnPrevActionLogs");
  const nextButton = document.getElementById("btnNextActionLogs");
  const pageInfo = document.getElementById("actionLogPageInfo");
  if (!prevButton || !nextButton || !pageInfo) return;

  prevButton.disabled = currentPage <= 1 || totalPages === 0;
  nextButton.disabled = currentPage >= totalPages || totalPages === 0;
  pageInfo.textContent = totalPages === 0 ? "0 / 0" : `${currentPage} / ${totalPages}`;
}

function renderActionLogRow(log) {
  return `
    <tr>
      <td>${escapeHtml(log.admin_action_log_id)}</td>
      <td>${renderAdmin(log.admin_name, log.admin_email)}</td>
      <td><span class="action-log-type">${escapeHtml(actionTypeLabel(log.action_type))}</span></td>
      <td>${renderTarget(log.target_type, log.target_id)}</td>
      <td class="action-log-detail">${escapeHtml(detailLabel(log.action_detail))}</td>
      <td>${escapeHtml(formatDateTime(log.created_at))}</td>
    </tr>
  `;
}

function renderAdmin(name, email) {
  return `
    <div class="action-log-admin">
      <strong>${escapeHtml(name)}</strong>
      <span>${escapeHtml(email)}</span>
    </div>
  `;
}

function renderTarget(type, id) {
  const targetLabel = targetTypeLabel(type);
  const targetHref = targetDetailHref(type, id);
  const targetContent = targetHref
    ? `<a href="${escapeHtml(targetHref)}">#${escapeHtml(id)}</a>`
    : `<span>#${escapeHtml(id)}</span>`;

  return `
    <div class="action-log-target">
      <strong>${escapeHtml(targetLabel)}</strong>
      ${targetContent}
    </div>
  `;
}

function targetDetailHref(type, id) {
  if (type === "MEMBER") {
    return `${contextPath}/members/${encodeURIComponent(id)}`;
  }
  if (type === "REPORT") {
    return `${contextPath}/admin?report_id=${encodeURIComponent(id)}#admin-report-${encodeURIComponent(id)}`;
  }
  return "";
}

function actionTypeLabel(value) {
  if (value === "MEMBER_BANNED") return "회원 정지";
  if (value === "MEMBER_UNBANNED") return "회원 해제";
  if (value === "REPORT_RESOLVED") return "신고 처리";
  if (value === "MEMBER_PROMOTED_TO_ADMIN") return "관리자 승격";
  if (value === "MEMBER_DEMOTED_TO_USER") return "권한 회수";
  if (value === "MEMBER_DELETED") return "회원 탈퇴";
  return value || "-";
}

function targetTypeLabel(value) {
  if (value === "MEMBER") return "회원";
  if (value === "REPORT") return "신고";
  return value || "-";
}

function detailLabel(value) {
  if (value === "ACCEPTED") return "신고 인정";
  if (value === "REJECTED") return "신고 반려";
  if (value === "NO_ACTION") return "조치 없음";
  return value || "-";
}

function formatDateTime(value) {
  if (!value) return "-";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "-";
  return date.toLocaleString("ko-KR");
}

function formatNumber(value) {
  return Number(value || 0).toLocaleString("ko-KR");
}

function escapeHtml(value) {
  return String(value ?? "-")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

import { apiRequest } from "../common/apiClient.js";

const contextPath = window.contextPath || "";
let pageSize = 20;
let currentPage = 1;
let totalPages = 0;

document.addEventListener("DOMContentLoaded", async () => {
  bindPageSizeSelect();
  bindPaginationButtons();
  await loadActionLogs();
});

async function loadActionLogs() {
  const count = document.getElementById("adminActionLogCount");
  const tableBody = document.getElementById("adminActionLogTableBody");
  if (!count || !tableBody) return;

  try {
    const response = await apiRequest(`${contextPath}/api/admin/action-logs?page=${currentPage}&size=${pageSize}`, {
      method: "GET",
    });

    if (!response.ok) {
      throw new Error(`admin action log api failed: ${response.status}`);
    }

    const data = await response.json();
    const logs = Array.isArray(data.items) ? data.items : [];
    currentPage = Number(data.page || currentPage);
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

function bindPageSizeSelect() {
  const select = document.getElementById("actionLogPageSize");
  select?.addEventListener("change", async () => {
    pageSize = Number(select.value || 20);
    currentPage = 1;
    await loadActionLogs();
  });
}

function bindPaginationButtons() {
  document.getElementById("btnPrevActionLogs")?.addEventListener("click", async () => {
    if (currentPage <= 1) return;
    currentPage -= 1;
    await loadActionLogs();
  });

  document.getElementById("btnNextActionLogs")?.addEventListener("click", async () => {
    if (currentPage >= totalPages) return;
    currentPage += 1;
    await loadActionLogs();
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
  return `
    <div class="action-log-target">
      <strong>${escapeHtml(targetTypeLabel(type))}</strong>
      <span>#${escapeHtml(id)}</span>
    </div>
  `;
}

function actionTypeLabel(value) {
  if (value === "MEMBER_BANNED") return "회원 정지";
  if (value === "MEMBER_UNBANNED") return "회원 해제";
  if (value === "REPORT_RESOLVED") return "신고 처리";
  if (value === "MEMBER_PROMOTED_TO_ADMIN") return "관리자 승격";
  if (value === "MEMBER_DEMOTED_TO_USER") return "권한 회수";
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

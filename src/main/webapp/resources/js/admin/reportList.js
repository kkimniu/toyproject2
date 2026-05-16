import { apiRequest } from "../common/apiClient.js";

const contextPath = window.contextPath || "";
let reports = [];
let activeStatus = "ALL";

document.addEventListener("DOMContentLoaded", async () => {
  bindFilterButtons();
  await loadReports();
});

async function loadReports() {
  const count = document.getElementById("adminReportCount");
  const tableBody = document.getElementById("adminReportTableBody");
  if (!count || !tableBody) return;

  try {
    const response = await apiRequest(`${contextPath}/api/admin/reports?page=1&size=20`, {
      method: "GET",
    });

    if (!response.ok) {
      throw new Error(`admin report api failed: ${response.status}`);
    }

    const data = await response.json();
    reports = Array.isArray(data.items) ? data.items : [];
    count.textContent = `전체 신고 ${formatNumber(data.total_count || 0)}건`;
    renderReports();
  } catch (error) {
    console.error(error);
    count.textContent = "신고 목록을 불러오지 못했습니다.";
    tableBody.innerHTML = `
      <tr class="data-table-empty">
        <td colspan="6">신고 목록을 불러오지 못했습니다.</td>
      </tr>
    `;
  }
}

function bindFilterButtons() {
  document.querySelectorAll(".report-filter-btn").forEach((button) => {
    button.addEventListener("click", () => {
      activeStatus = button.dataset.status || "ALL";
      document.querySelectorAll(".report-filter-btn").forEach((item) => {
        item.classList.toggle("is-active", item === button);
      });
      renderReports();
    });
  });
}

function renderReports() {
  const tableBody = document.getElementById("adminReportTableBody");
  if (!tableBody) return;

  const visibleReports = activeStatus === "ALL"
    ? reports
    : reports.filter((report) => report.status === activeStatus);

  if (visibleReports.length === 0) {
    tableBody.innerHTML = `
      <tr class="data-table-empty">
        <td colspan="6">${emptyMessage()}</td>
      </tr>
    `;
    return;
  }

  tableBody.innerHTML = visibleReports.map(renderReportRow).join("");
}

function renderReportRow(report) {
  return `
    <tr>
      <td>${escapeHtml(report.report_id)}</td>
      <td>${renderParty(report.target_member_name, report.target_member_email)}</td>
      <td>${renderParty(report.reporter_name, report.reporter_email)}</td>
      <td class="report-reason">${escapeHtml(report.reason)}</td>
      <td><span class="report-status ${statusClass(report.status)}">${escapeHtml(statusLabel(report.status))}</span></td>
      <td>${escapeHtml(formatDate(report.report_created_at))}</td>
    </tr>
  `;
}

function renderParty(name, email) {
  return `
    <div class="report-party">
      <strong>${escapeHtml(name)}</strong>
      <span>${escapeHtml(email)}</span>
    </div>
  `;
}

function emptyMessage() {
  if (activeStatus === "PENDING") return "대기 중인 신고가 없습니다.";
  if (activeStatus === "RESOLVED") return "처리 완료된 신고가 없습니다.";
  return "표시할 신고가 없습니다.";
}

function statusClass(status) {
  if (status === "PENDING") return "report-status-pending";
  if (status === "RESOLVED") return "report-status-resolved";
  return "";
}

function statusLabel(status) {
  if (status === "PENDING") return "대기";
  if (status === "RESOLVED") return "처리완료";
  return status || "-";
}

function formatDate(value) {
  if (!value) return "-";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "-";
  return date.toLocaleDateString("ko-KR");
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

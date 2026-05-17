import { apiRequest } from "../common/apiClient.js";

const contextPath = window.contextPath || "";
let reports = [];
let currentPage = 1;
let totalPages = 1;
let currentFilters = {};
let selectedReportId = null;
let pageSize = 20;
let currentPage = 1;
let totalPages = 0;

document.addEventListener("DOMContentLoaded", async () => {
  bindPageSizeSelect();
  bindFilterButtons();
  bindPaginationButtons();
  bindResolutionModal();
  await loadReports();
});

async function loadReports(page = currentPage) {
  const count = document.getElementById("adminReportCount");
  const tableBody = document.getElementById("adminReportTableBody");
  if (!count || !tableBody) return;

  try {
    const response = await apiRequest(`${contextPath}/api/admin/reports?page=${currentPage}&size=${pageSize}`, {
      method: "GET",
    });

    if (!response.ok) {
      throw new Error(`admin report api failed: ${response.status}`);
    }

    const data = await response.json();
    reports = Array.isArray(data.items) ? data.items : [];
    currentPage = Number(data.page || currentPage);
    totalPages = Number(data.total_pages || 0);
    count.textContent = `전체 신고 ${formatNumber(data.total_count || 0)}건`;
    renderPagination();
    renderReports();
    renderPagination();
  } catch (error) {
    console.error(error);
    count.textContent = "신고 목록을 불러오지 못했습니다.";
    tableBody.innerHTML = `
      <tr class="data-table-empty">
        <td colspan="7">신고 목록을 불러오지 못했습니다.</td>
      </tr>
    `;
    totalPages = 0;
    renderPagination();
  }
}

function bindPageSizeSelect() {
  const select = document.getElementById("reportPageSize");
  select?.addEventListener("change", async () => {
    pageSize = Number(select.value || 20);
    currentPage = 1;
    await loadReports();
  });
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

function bindPaginationButtons() {
  const prevButton = document.getElementById("btnPrevReports");
  const nextButton = document.getElementById("btnNextReports");

  prevButton?.addEventListener("click", async () => {
    if (currentPage <= 1) return;
    currentPage -= 1;
    await loadReports();
  });

  nextButton?.addEventListener("click", async () => {
    if (currentPage >= totalPages) return;
    currentPage += 1;
    await loadReports();
  });
}

function bindResolutionModal() {
  const modal = document.getElementById("reportResolutionModal");
  const form = document.getElementById("reportResolutionForm");
  if (!modal || !form) return;

  modal.querySelectorAll("[data-close-resolution-modal]").forEach((button) => {
    button.addEventListener("click", closeResolutionModal);
  });

  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    await submitResolution(form);
  });
}

function renderReports() {
  const tableBody = document.getElementById("adminReportTableBody");
  if (!tableBody) return;

  if (reports.length === 0) {
    tableBody.innerHTML = `
      <tr class="data-table-empty">
        <td colspan="7">검색 조건에 맞는 신고가 없습니다.</td>
      </tr>
    `;
    return;
  }

  tableBody.innerHTML = reports.map(renderReportRow).join("");
  bindActionButtons(tableBody);
}

function renderPagination() {
  const prevButton = document.getElementById("btnPrevReports");
  const nextButton = document.getElementById("btnNextReports");
  const pageInfo = document.getElementById("reportPageInfo");
  if (!prevButton || !nextButton || !pageInfo) return;

  prevButton.disabled = currentPage <= 1 || totalPages === 0;
  nextButton.disabled = currentPage >= totalPages || totalPages === 0;
  pageInfo.textContent = totalPages === 0 ? "0 / 0" : `${currentPage} / ${totalPages}`;
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
      <td>${renderActionCell(report)}</td>
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

function renderActionCell(report) {
  if (report.status === "RESOLVED") {
    return '<span class="report-action-empty">-</span>';
  }

  return `
    <button
      type="button"
      class="report-action-btn"
      data-report-id="${escapeHtml(report.report_id)}">
      처리
    </button>
  `;
}

function bindActionButtons(container) {
  container.querySelectorAll(".report-action-btn").forEach((button) => {
    button.addEventListener("click", () => {
      openResolutionModal(button.dataset.reportId);
    });
  });
}

function openResolutionModal(reportId) {
  const modal = document.getElementById("reportResolutionModal");
  const form = document.getElementById("reportResolutionForm");
  if (!modal || !form || !reportId) return;

  selectedReportId = reportId;
  form.reset();
  modal.classList.add("is-open");
  modal.setAttribute("aria-hidden", "false");
}

function closeResolutionModal() {
  const modal = document.getElementById("reportResolutionModal");
  const form = document.getElementById("reportResolutionForm");
  if (!modal || !form) return;

  selectedReportId = null;
  form.reset();
  modal.classList.remove("is-open");
  modal.setAttribute("aria-hidden", "true");
}

async function submitResolution(form) {
  if (!selectedReportId) return;

  const submitButton = form.querySelector('button[type="submit"]');
  const formData = new FormData(form);
  const resolutionType = String(formData.get("resolution_type") || "");
  const resolutionMessage = String(formData.get("resolution_message") || "").trim();

  if (!resolutionType || !resolutionMessage) return;

  submitButton.disabled = true;

  try {
    const response = await apiRequest(`${contextPath}/api/admin/reports/${encodeURIComponent(selectedReportId)}/status`, {
      method: "PATCH",
      body: JSON.stringify({
        status: "RESOLVED",
        resolution_type: resolutionType,
        resolution_message: resolutionMessage,
      }),
    });

    if (!response.ok) {
      throw new Error(`admin report status api failed: ${response.status}`);
    }

    closeResolutionModal();
    await loadReports(currentPage);
  } catch (error) {
    console.error(error);
    alert("신고 상태를 변경하지 못했습니다.");
  } finally {
    submitButton.disabled = false;
  }
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

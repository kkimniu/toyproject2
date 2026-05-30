import { apiRequest } from "../common/apiClient.js";

const contextPath = window.contextPath || "";
const selectedCandidateIds = new Set();
let currentTrendDays = null;
let currentReportTrends = [];

document.addEventListener("DOMContentLoaded", async () => {
  bindDashboardLinks();
  bindSanctionBulkActions();
  bindDashboardSettings();
  bindReportTrendCsv();
  await loadDashboardSummary();
});

export async function loadDashboardSummary() {
  try {
    const params = new URLSearchParams();
    if (currentTrendDays) {
      params.set("trend_days", String(currentTrendDays));
    }
    const query = params.toString() ? `?${params.toString()}` : "";
    const response = await apiRequest(`${contextPath}/api/admin/dashboard/summary${query}`, {
      method: "GET",
    });

    if (!response.ok) {
      throw new Error(`admin dashboard summary api failed: ${response.status}`);
    }

    const data = await response.json();
    selectedCandidateIds.clear();
    currentTrendDays = Number(data.settings?.report_trend_days || currentTrendDays || 7);
    setValue("summaryTotalMembers", data.total_members);
    setValue("summaryBannedMembers", data.banned_members);
    setValue("summaryPendingReports", data.pending_reports);
    setValue("summaryResolvedReports", data.resolved_reports);
    setValue("summaryActionRequired", data.action_required_count);
    renderOperationAlerts(data.operation_alerts);
    renderSanctionCandidates(data.sanction_candidates);
    renderReportTrends(data.report_trends);
    renderDashboardSettings(data.settings);
  } catch (error) {
    console.error(error);
    ["summaryTotalMembers", "summaryBannedMembers", "summaryPendingReports", "summaryResolvedReports", "summaryActionRequired"]
      .forEach((id) => setText(id, "-"));
    renderOperationAlerts([]);
    renderSanctionCandidates([]);
    renderReportTrends([]);
  }
}

function bindDashboardSettings() {
  document.getElementById("reportTrendDaysSelect")?.addEventListener("change", async (event) => {
    currentTrendDays = Number(event.target.value || 7);
    await loadDashboardSummary();
  });

  document.getElementById("btnSaveDashboardSettings")?.addEventListener("click", saveDashboardSettings);
}

function bindReportTrendCsv() {
  document.getElementById("btnExportReportTrendCsv")?.addEventListener("click", exportReportTrendCsv);
  syncReportTrendCsvButton();
}

function renderDashboardSettings(settings) {
  if (!settings) return;

  const thresholdInput = document.getElementById("sanctionThresholdInput");
  const trendDaysSelect = document.getElementById("reportTrendDaysSelect");
  if (thresholdInput) {
    thresholdInput.value = String(settings.sanction_candidate_report_threshold || 3);
  }
  if (trendDaysSelect) {
    trendDaysSelect.value = String(settings.report_trend_days || currentTrendDays || 7);
  }
}

async function saveDashboardSettings() {
  const thresholdInput = document.getElementById("sanctionThresholdInput");
  const trendDaysSelect = document.getElementById("reportTrendDaysSelect");
  const threshold = Number(thresholdInput?.value || 3);
  const trendDays = Number(trendDaysSelect?.value || currentTrendDays || 7);

  if (!Number.isInteger(threshold) || threshold < 1 || threshold > 20) {
    alert("제재 후보 신고 기준은 1~20 사이로 입력해야 합니다.");
    return;
  }
  if (!Number.isInteger(trendDays) || trendDays < 7 || trendDays > 90) {
    alert("신고 추이 기간은 7~90일 사이여야 합니다.");
    return;
  }

  const button = document.getElementById("btnSaveDashboardSettings");
  if (button) button.disabled = true;

  try {
    const response = await apiRequest(`${contextPath}/api/admin/dashboard/settings`, {
      method: "PATCH",
      body: JSON.stringify({
        sanction_candidate_report_threshold: threshold,
        report_trend_days: trendDays,
      }),
    });
    if (!response.ok) {
      throw new Error(`dashboard settings api failed: ${response.status}`);
    }
    const settings = await response.json();
    currentTrendDays = Number(settings.report_trend_days || trendDays);
    await loadDashboardSummary();
    alert("운영 기준을 저장했습니다.");
  } catch (error) {
    console.error(error);
    alert("운영 기준을 저장하지 못했습니다.");
  } finally {
    if (button) button.disabled = false;
  }
}

function bindDashboardLinks(root = document) {
  root.querySelectorAll("[data-dashboard-panel]").forEach((button) => {
    button.addEventListener("click", () => {
      const panel = button.dataset.dashboardPanel;
      if (panel) {
        window.location.hash = panel;
      }
    });
  });
}

function renderOperationAlerts(items) {
  const container = document.getElementById("adminOperationAlerts");
  if (!container) return;

  const alerts = Array.isArray(items) ? items : [];
  if (alerts.length === 0) {
    container.innerHTML = '<p class="dashboard-empty">현재 확인 필요 항목이 없습니다.</p>';
    return;
  }

  container.innerHTML = alerts.map((item) => `
    <button type="button" class="dashboard-alert-item" data-dashboard-panel="${escapeHtml(item.target_panel || "dashboard")}">
      <span class="dashboard-alert-top">
        <span class="dashboard-alert-title">${escapeHtml(item.title || "-")}</span>
        <span class="dashboard-count-badge">${formatNumber(item.count)}건</span>
      </span>
      <span class="dashboard-alert-description">${escapeHtml(item.description || "-")}</span>
    </button>
  `).join("");
  bindDashboardLinks(container);
}

function renderSanctionCandidates(items) {
  const container = document.getElementById("adminSanctionCandidates");
  if (!container) return;

  const candidates = Array.isArray(items) ? items : [];
  if (candidates.length === 0) {
    container.innerHTML = '<p class="dashboard-empty">신고 누적 제재 후보가 없습니다.</p>';
    syncSanctionBulkButtons();
    return;
  }

  container.innerHTML = candidates.map((item) => `
    <article class="sanction-candidate-item">
      <div class="sanction-candidate-top">
        <label class="sanction-candidate-check">
          <input type="checkbox" data-sanction-candidate-id="${escapeHtml(item.member_id)}">
          <span>
            <span class="sanction-candidate-name">${escapeHtml(item.name || "-")}</span>
            <span class="sanction-candidate-meta">${escapeHtml(item.email || "-")} · ${escapeHtml(statusLabel(item.status))}</span>
          </span>
        </label>
        <span class="sanction-risk-badge">신고 ${formatNumber(item.total_reports)}건</span>
      </div>
      <div class="sanction-candidate-stats">
        <span>대기 ${formatNumber(item.pending_reports)}건</span>
        <span>인정 ${formatNumber(item.accepted_reports)}건</span>
        <span>정지 ${formatNumber(item.ban_count)}회</span>
      </div>
    </article>
  `).join("");
  bindSanctionCandidateSelection(container);
  syncSanctionBulkButtons();
}

function bindSanctionCandidateSelection(container) {
  container.querySelectorAll("[data-sanction-candidate-id]").forEach((checkbox) => {
    checkbox.addEventListener("change", () => {
      const memberId = checkbox.dataset.sanctionCandidateId;
      if (!memberId) return;
      if (checkbox.checked) {
        selectedCandidateIds.add(memberId);
      } else {
        selectedCandidateIds.delete(memberId);
      }
      syncSanctionBulkButtons();
    });
  });
}

function bindSanctionBulkActions() {
  document.getElementById("btnSanctionBanSelected")?.addEventListener("click", async () => {
    await submitBulkSanction("BANNED");
  });

  document.getElementById("btnSanctionDeleteSelected")?.addEventListener("click", async () => {
    await submitBulkDelete();
  });
  syncSanctionBulkButtons();
}

function syncSanctionBulkButtons() {
  const disabled = selectedCandidateIds.size === 0;
  const banButton = document.getElementById("btnSanctionBanSelected");
  const deleteButton = document.getElementById("btnSanctionDeleteSelected");
  if (banButton) banButton.disabled = disabled;
  if (deleteButton) deleteButton.disabled = disabled;
}

async function submitBulkSanction(nextStatus) {
  const memberIds = [...selectedCandidateIds];
  if (memberIds.length === 0) return;
  if (!confirm(`선택한 제재 후보 ${memberIds.length}명을 정지 처리하시겠습니까?`)) return;

  await runBulkAction(memberIds, async (memberId) => {
    const response = await apiRequest(`${contextPath}/api/admin/members/${encodeURIComponent(memberId)}/status`, {
      method: "PATCH",
      body: JSON.stringify({ status: nextStatus }),
    });
    if (!response.ok) {
      throw new Error(`candidate ban failed: ${memberId}`);
    }
  }, "선택한 제재 후보 정지 처리를 완료했습니다.", "일부 제재 후보를 정지 처리하지 못했습니다.");
}

async function submitBulkDelete() {
  const memberIds = [...selectedCandidateIds];
  if (memberIds.length === 0) return;
  if (!confirm(`선택한 제재 후보 ${memberIds.length}명을 탈퇴 처리하시겠습니까?`)) return;

  await runBulkAction(memberIds, async (memberId) => {
    const response = await apiRequest(`${contextPath}/api/admin/members/${encodeURIComponent(memberId)}`, {
      method: "DELETE",
    });
    if (!response.ok) {
      throw new Error(`candidate delete failed: ${memberId}`);
    }
  }, "선택한 제재 후보 탈퇴 처리를 완료했습니다.", "일부 제재 후보를 탈퇴 처리하지 못했습니다.");
}

async function runBulkAction(memberIds, action, successMessage, failureMessage) {
  setSanctionBulkDisabled(true);
  let failed = 0;

  for (const memberId of memberIds) {
    try {
      await action(memberId);
    } catch (error) {
      failed += 1;
      console.error(error);
    }
  }

  selectedCandidateIds.clear();
  await loadDashboardSummary();
  if (failed > 0) {
    alert(`${failureMessage} 실패 ${failed}건`);
  } else {
    alert(successMessage);
  }
}

function setSanctionBulkDisabled(disabled) {
  const banButton = document.getElementById("btnSanctionBanSelected");
  const deleteButton = document.getElementById("btnSanctionDeleteSelected");
  if (banButton) banButton.disabled = disabled;
  if (deleteButton) deleteButton.disabled = disabled;
}

function renderReportTrends(items) {
  const container = document.getElementById("adminReportTrends");
  if (!container) return;

  const trends = Array.isArray(items) ? items : [];
  currentReportTrends = trends;
  syncReportTrendCsvButton();
  if (trends.length === 0) {
    container.innerHTML = '<p class="dashboard-empty">최근 신고 추이가 없습니다.</p>';
    return;
  }

  const maxValue = Math.max(
    1,
    ...trends.flatMap((item) => [Number(item.created_reports || 0), Number(item.resolved_reports || 0)])
  );

  container.innerHTML = trends.map((item) => {
    const created = Number(item.created_reports || 0);
    const resolved = Number(item.resolved_reports || 0);
    return `
      <article class="report-trend-item">
        <div class="report-trend-date">${escapeHtml(formatTrendDate(item.trend_date))}</div>
        <div class="report-trend-bars">
          ${renderTrendBar("접수", created, maxValue, "created")}
          ${renderTrendBar("처리", resolved, maxValue, "resolved")}
        </div>
      </article>
    `;
  }).join("");
}

function renderTrendBar(label, value, maxValue, type) {
  const width = Math.max(4, Math.round((Number(value || 0) / maxValue) * 100));
  return `
    <div class="report-trend-bar-row">
      <span class="report-trend-label">${escapeHtml(label)}</span>
      <span class="report-trend-track">
        <span class="report-trend-fill report-trend-fill-${escapeHtml(type)}" style="width: ${width}%"></span>
      </span>
      <strong>${formatNumber(value)}</strong>
    </div>
  `;
}

function syncReportTrendCsvButton() {
  const button = document.getElementById("btnExportReportTrendCsv");
  if (button) {
    button.disabled = currentReportTrends.length === 0;
  }
}

function exportReportTrendCsv() {
  if (currentReportTrends.length === 0) return;

  const rows = [
    ["날짜", "접수 신고", "처리 신고"],
    ...currentReportTrends.map((item) => [
      item.trend_date || "",
      String(item.created_reports || 0),
      String(item.resolved_reports || 0),
    ]),
  ];
  const csv = rows.map((row) => row.map(escapeCsv).join(",")).join("\r\n");
  const blob = new Blob([`\uFEFF${csv}`], { type: "text/csv;charset=utf-8;" });
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement("a");
  anchor.href = url;
  anchor.download = `admin-report-trends-${currentTrendDays || 7}d.csv`;
  document.body.appendChild(anchor);
  anchor.click();
  anchor.remove();
  URL.revokeObjectURL(url);
}

function escapeCsv(value) {
  const text = String(value ?? "");
  if (/[",\r\n]/.test(text)) {
    return `"${text.replaceAll('"', '""')}"`;
  }
  return text;
}

function setValue(id, value) {
  setText(id, formatNumber(value));
}

function setText(id, value) {
  const element = document.getElementById(id);
  if (element) {
    element.textContent = value;
  }
}

function formatNumber(value) {
  return Number(value || 0).toLocaleString("ko-KR");
}

function formatTrendDate(value) {
  if (!value) return "-";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return String(value);
  return date.toLocaleDateString("ko-KR", { month: "numeric", day: "numeric" });
}

function statusLabel(status) {
  if (status === "ACTIVE") return "정상";
  if (status === "BANNED") return "정지";
  if (status === "DELETED") return "탈퇴";
  return status || "-";
}

function escapeHtml(value) {
  return String(value ?? "-")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

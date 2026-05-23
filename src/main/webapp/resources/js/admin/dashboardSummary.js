import { apiRequest } from "../common/apiClient.js";

const contextPath = window.contextPath || "";
const selectedCandidateIds = new Set();

document.addEventListener("DOMContentLoaded", async () => {
  bindDashboardLinks();
  bindSanctionBulkActions();
  await loadDashboardSummary();
});

export async function loadDashboardSummary() {
  try {
    const response = await apiRequest(`${contextPath}/api/admin/dashboard/summary`, {
      method: "GET",
    });

    if (!response.ok) {
      throw new Error(`admin dashboard summary api failed: ${response.status}`);
    }

    const data = await response.json();
    selectedCandidateIds.clear();
    setValue("summaryTotalMembers", data.total_members);
    setValue("summaryBannedMembers", data.banned_members);
    setValue("summaryPendingReports", data.pending_reports);
    setValue("summaryResolvedReports", data.resolved_reports);
    setValue("summaryActionRequired", data.action_required_count);
    renderOperationAlerts(data.operation_alerts);
    renderSanctionCandidates(data.sanction_candidates);
    renderReportTrends(data.report_trends);
  } catch (error) {
    console.error(error);
    ["summaryTotalMembers", "summaryBannedMembers", "summaryPendingReports", "summaryResolvedReports", "summaryActionRequired"]
      .forEach((id) => setText(id, "-"));
    renderOperationAlerts([]);
    renderSanctionCandidates([]);
    renderReportTrends([]);
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

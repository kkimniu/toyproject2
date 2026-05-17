import { apiRequest } from "../common/apiClient.js";

const contextPath = window.contextPath || "";

document.addEventListener("DOMContentLoaded", async () => {
  await loadDashboardSummary();
});

async function loadDashboardSummary() {
  try {
    const response = await apiRequest(`${contextPath}/api/admin/dashboard/summary`, {
      method: "GET",
    });

    if (!response.ok) {
      throw new Error(`admin dashboard summary api failed: ${response.status}`);
    }

    const data = await response.json();
    setValue("summaryTotalMembers", data.total_members);
    setValue("summaryBannedMembers", data.banned_members);
    setValue("summaryPendingReports", data.pending_reports);
    setValue("summaryResolvedReports", data.resolved_reports);
  } catch (error) {
    console.error(error);
    ["summaryTotalMembers", "summaryBannedMembers", "summaryPendingReports", "summaryResolvedReports"]
      .forEach((id) => setText(id, "-"));
  }
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

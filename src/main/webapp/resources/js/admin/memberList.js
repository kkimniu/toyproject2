import { apiRequest } from "../common/apiClient.js";

const contextPath = window.contextPath || "";

document.addEventListener("DOMContentLoaded", async () => {
  await loadMembers();
});

async function loadMembers() {
  const count = document.getElementById("adminMemberCount");
  const tableBody = document.getElementById("adminMemberTableBody");
  if (!count || !tableBody) return;

  try {
    const response = await apiRequest(`${contextPath}/api/admin/members?page=1&size=20`, {
      method: "GET",
    });

    if (!response.ok) {
      throw new Error(`admin member api failed: ${response.status}`);
    }

    const data = await response.json();
    const members = Array.isArray(data.items) ? data.items : [];
    count.textContent = `전체 회원 ${formatNumber(data.total_count || 0)}명`;

    if (members.length === 0) {
      tableBody.innerHTML = `
        <tr class="member-table-empty">
          <td colspan="6">표시할 회원이 없습니다.</td>
        </tr>
      `;
      return;
    }

    tableBody.innerHTML = members.map(renderMemberRow).join("");
  } catch (error) {
    console.error(error);
    count.textContent = "회원 목록을 불러오지 못했습니다.";
    tableBody.innerHTML = `
      <tr class="member-table-empty">
        <td colspan="6">회원 목록을 불러오지 못했습니다.</td>
      </tr>
    `;
  }
}

function renderMemberRow(member) {
  const role = member.role || "-";
  const status = member.status || "-";

  return `
    <tr>
      <td>${escapeHtml(member.member_id)}</td>
      <td>${escapeHtml(member.email)}</td>
      <td>${escapeHtml(member.name)}</td>
      <td><span class="member-role">${escapeHtml(role)}</span></td>
      <td><span class="member-status ${statusClass(status)}">${escapeHtml(status)}</span></td>
      <td>${escapeHtml(formatDate(member.member_created_at))}</td>
    </tr>
  `;
}

function statusClass(status) {
  if (status === "ACTIVE") return "member-status-active";
  if (status === "BANNED") return "member-status-banned";
  if (status === "DELETED") return "member-status-deleted";
  return "";
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

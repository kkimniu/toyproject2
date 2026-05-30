import { apiRequest } from "../common/apiClient.js";
import { loadDashboardSummary } from "./dashboardSummary.js";

const contextPath = window.contextPath || "";
const REPEAT_PENALTY_THRESHOLD = 3;
let pageSize = 20;
let currentPage = 1;
let totalPages = 0;
let currentAdminId = null;
let currentAdminRole = null;
let currentFilters = {};

document.addEventListener("DOMContentLoaded", async () => {
  await loadCurrentAdmin();
  bindSearchForm();
  bindPageSizeSelect();
  bindPaginationButtons();
  await loadMembers();
});

async function loadCurrentAdmin() {
  try {
    const response = await apiRequest(`${contextPath}/api/members/me`, {
      method: "GET",
    });
    if (!response.ok) return;

    const member = await response.json();
    currentAdminId = member.member_id;
    currentAdminRole = member.member_role_enum;
  } catch (error) {
    console.error(error);
  }
}

async function loadMembers(page = currentPage) {
  const count = document.getElementById("adminMemberCount");
  const tableBody = document.getElementById("adminMemberTableBody");
  if (!count || !tableBody) return;

  try {
    const params = new URLSearchParams({
      page: String(page),
      size: String(pageSize),
      ...currentFilters,
    });
    const response = await apiRequest(`${contextPath}/api/admin/members?${params.toString()}`, {
      method: "GET",
    });

    if (!response.ok) {
      throw new Error(`admin member api failed: ${response.status}`);
    }

    const data = await response.json();
    const members = Array.isArray(data.items) ? data.items : [];
    currentPage = Number(data.page || 1);
    totalPages = Number(data.total_pages || 0);
    renderPagination();
    count.textContent = `전체 회원 ${formatNumber(data.total_count || 0)}명`;

    if (members.length === 0) {
      tableBody.innerHTML = `
        <tr class="member-table-empty">
          <td colspan="8">표시할 회원이 없습니다.</td>
        </tr>
      `;
      return;
    }

    tableBody.innerHTML = members.map(renderMemberRow).join("");
    bindActionButtons(tableBody);
  } catch (error) {
    console.error(error);
    count.textContent = "회원 목록을 불러오지 못했습니다.";
    tableBody.innerHTML = `
      <tr class="member-table-empty">
        <td colspan="8">회원 목록을 불러오지 못했습니다.</td>
      </tr>
    `;
    totalPages = 0;
    renderPagination();
  }
}

function bindSearchForm() {
  const form = document.getElementById("memberSearchForm");
  form?.addEventListener("submit", async (event) => {
    event.preventDefault();
    const formData = new FormData(form);
    currentFilters = Object.fromEntries(
      [...formData.entries()]
        .map(([key, value]) => [key, String(value).trim()])
        .filter(([, value]) => value)
    );
    currentPage = 1;
    await loadMembers(1);
  });
}

function bindPaginationButtons() {
  const prevButton = document.getElementById("btnPrevMembers");
  const nextButton = document.getElementById("btnNextMembers");

  prevButton?.addEventListener("click", async () => {
    if (currentPage <= 1) return;
    await loadMembers(currentPage - 1);
  });

  nextButton?.addEventListener("click", async () => {
    if (currentPage >= totalPages) return;
    await loadMembers(currentPage + 1);
  });
}

function bindPageSizeSelect() {
  const select = document.getElementById("memberPageSize");
  select?.addEventListener("change", async () => {
    pageSize = Number(select.value || 20);
    currentPage = 1;
    await loadMembers(1);
  });
}

function renderPagination() {
  const prevButton = document.getElementById("btnPrevMembers");
  const nextButton = document.getElementById("btnNextMembers");
  const pageInfo = document.getElementById("memberPageInfo");
  if (!prevButton || !nextButton || !pageInfo) return;

  prevButton.disabled = currentPage <= 1 || totalPages === 0;
  nextButton.disabled = currentPage >= totalPages || totalPages === 0;
  pageInfo.textContent = totalPages === 0 ? "0 / 0" : `${currentPage} / ${totalPages}`;
}

function renderMemberRow(member) {
  const role = member.role || "-";
  const status = member.status || "-";
  const banCount = Number(member.ban_count || 0);
  const repeatPenaltyClass = isRepeatPenaltyMember(banCount) ? " member-row-repeat-penalty" : "";

  return `
    <tr class="${repeatPenaltyClass.trim()}">
      <td>${escapeHtml(member.member_id)}</td>
      <td>${escapeHtml(member.email)}</td>
      <td>${escapeHtml(member.name)}</td>
      <td><span class="member-role">${escapeHtml(role)}</span></td>
      <td><span class="member-status ${statusClass(status)}">${escapeHtml(status)}</span></td>
      <td>${renderBanCount(banCount)}</td>
      <td>${escapeHtml(formatDate(member.member_created_at))}</td>
      <td>${renderActionCell(member)}</td>
    </tr>
  `;
}

function renderBanCount(banCount) {
  if (!isRepeatPenaltyMember(banCount)) {
    return escapeHtml(formatNumber(banCount));
  }

  return `
    <div class="member-ban-count">
      <strong>${escapeHtml(formatNumber(banCount))}</strong>
      <span>반복 제재</span>
    </div>
  `;
}

function isRepeatPenaltyMember(banCount) {
  return banCount >= REPEAT_PENALTY_THRESHOLD;
}

function renderActionCell(member) {
  if (member.role === "SUPER_ADMIN" || member.member_id === currentAdminId) {
    return '<span class="member-action-empty">-</span>';
  }

  if (member.role === "ADMIN") {
    if (currentAdminRole !== "SUPER_ADMIN") {
      return '<span class="member-action-empty">-</span>';
    }

    return `
      <div class="member-actions">
        ${renderStatusActionButton(member)}
        <button
          type="button"
          class="member-action-btn"
          data-member-id="${escapeHtml(member.member_id)}"
          data-next-role="USER">
          권한 회수
        </button>
        ${renderDeleteActionButton(member)}
      </div>
    `;
  }

  if (member.role !== "USER") {
    return '<span class="member-action-empty">-</span>';
  }

  const roleAction = currentAdminRole === "SUPER_ADMIN"
    ? `
      <button
        type="button"
        class="member-action-btn"
        data-member-id="${escapeHtml(member.member_id)}"
        data-next-role="ADMIN">
        관리자 승격
      </button>
    `
    : "";
  return `
    <div class="member-actions">
      ${renderStatusActionButton(member)}
      ${roleAction}
      ${renderDeleteActionButton(member)}
    </div>
  `;
}

function renderStatusActionButton(member) {
  const nextStatus = member.status === "BANNED" ? "ACTIVE" : "BANNED";
  const label = nextStatus === "BANNED" ? "정지" : "해제";
  return `
    <button
      type="button"
      class="member-action-btn"
      data-member-id="${escapeHtml(member.member_id)}"
      data-next-status="${escapeHtml(nextStatus)}">
      ${label}
    </button>
  `;
}

function renderDeleteActionButton(member) {
  if (member.status === "DELETED" || member.role === "SUPER_ADMIN") {
    return "";
  }
  if (member.role === "ADMIN" && currentAdminRole !== "SUPER_ADMIN") {
    return "";
  }

  return `
    <button
      type="button"
      class="member-action-btn member-delete-btn"
      data-member-id="${escapeHtml(member.member_id)}">
      탈퇴
    </button>
  `;
}

function bindActionButtons(container) {
  container.querySelectorAll(".member-action-btn").forEach((button) => {
    button.addEventListener("click", async () => {
      await updateMemberStatus(button);
    });
  });

  container.querySelectorAll(".member-action-btn[data-next-role]").forEach((button) => {
    button.addEventListener("click", async () => {
      await updateMemberRole(button);
    });
  });

  container.querySelectorAll(".member-delete-btn").forEach((button) => {
    button.addEventListener("click", async () => {
      await deleteMember(button);
    });
  });
}

async function updateMemberStatus(button) {
  const memberId = button.dataset.memberId;
  const nextStatus = button.dataset.nextStatus;
  if (!memberId || !nextStatus) return;

  const ok = confirm(nextStatus === "BANNED" ? "이 회원을 정지하시겠습니까?" : "이 회원의 정지를 해제하시겠습니까?");
  if (!ok) return;

  button.disabled = true;

  try {
    const response = await apiRequest(`${contextPath}/api/admin/members/${encodeURIComponent(memberId)}/status`, {
      method: "PATCH",
      body: JSON.stringify({ status: nextStatus }),
    });

    if (!response.ok) {
      throw new Error(`admin member status api failed: ${response.status}`);
    }

    const updatedMember = await response.json();
    const row = button.closest("tr");
    if (!row) return;
    row.outerHTML = renderMemberRow(updatedMember);
    bindActionButtons(document.getElementById("adminMemberTableBody"));
    await loadDashboardSummary();
  } catch (error) {
    console.error(error);
    alert("회원 상태를 변경하지 못했습니다.");
    button.disabled = false;
  }
}

async function updateMemberRole(button) {
  const memberId = button.dataset.memberId;
  const nextRole = button.dataset.nextRole;
  if (!memberId || !nextRole) return;

  const ok = confirm(nextRole === "ADMIN"
    ? "이 회원을 관리자로 승격하시겠습니까?"
    : "이 관리자의 권한을 회수하시겠습니까?");
  if (!ok) return;

  button.disabled = true;

  try {
    const response = await apiRequest(`${contextPath}/api/admin/members/${encodeURIComponent(memberId)}/role`, {
      method: "PATCH",
      body: JSON.stringify({ role: nextRole }),
    });

    if (!response.ok) {
      throw new Error(`admin member role api failed: ${response.status}`);
    }

    const updatedMember = await response.json();
    const row = button.closest("tr");
    if (!row) return;
    row.outerHTML = renderMemberRow(updatedMember);
    bindActionButtons(document.getElementById("adminMemberTableBody"));
  } catch (error) {
    console.error(error);
    alert("회원 권한을 변경하지 못했습니다.");
    button.disabled = false;
  }
}

async function deleteMember(button) {
  const memberId = button.dataset.memberId;
  if (!memberId) return;

  const ok = confirm("이 회원을 탈퇴 처리하시겠습니까? 되돌릴 수 없는 운영 조치입니다.");
  if (!ok) return;

  button.disabled = true;

  try {
    const response = await apiRequest(`${contextPath}/api/admin/members/${encodeURIComponent(memberId)}`, {
      method: "DELETE",
    });

    if (!response.ok) {
      throw new Error(`admin member delete api failed: ${response.status}`);
    }

    await loadMembers(currentPage);
    await loadDashboardSummary();
  } catch (error) {
    console.error(error);
    alert("회원 탈퇴 처리를 완료하지 못했습니다.");
    button.disabled = false;
  }
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

import { apiRequest } from "../common/apiClient.js";

const contextPath = window.contextPath || "";
let pageSize = 20;
let currentPage = 1;
let totalPages = 0;

document.addEventListener("DOMContentLoaded", async () => {
  bindPageSizeSelect();
  bindPaginationButtons();
  await loadMembers();
});

async function loadMembers() {
  const count = document.getElementById("adminMemberCount");
  const tableBody = document.getElementById("adminMemberTableBody");
  if (!count || !tableBody) return;

  try {
    const response = await apiRequest(`${contextPath}/api/admin/members?page=${currentPage}&size=${pageSize}`, {
      method: "GET",
    });

    if (!response.ok) {
      throw new Error(`admin member api failed: ${response.status}`);
    }

    const data = await response.json();
    const members = Array.isArray(data.items) ? data.items : [];
    currentPage = Number(data.page || currentPage);
    totalPages = Number(data.total_pages || 0);
    renderPagination();
    count.textContent = `전체 회원 ${formatNumber(data.total_count || 0)}명`;

    if (members.length === 0) {
      tableBody.innerHTML = `
        <tr class="member-table-empty">
          <td colspan="7">표시할 회원이 없습니다.</td>
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
        <td colspan="7">회원 목록을 불러오지 못했습니다.</td>
      </tr>
    `;
    totalPages = 0;
    renderPagination();
  }
}

function bindPaginationButtons() {
  const prevButton = document.getElementById("btnPrevMembers");
  const nextButton = document.getElementById("btnNextMembers");

  prevButton?.addEventListener("click", async () => {
    if (currentPage <= 1) return;
    currentPage -= 1;
    await loadMembers();
  });

  nextButton?.addEventListener("click", async () => {
    if (currentPage >= totalPages) return;
    currentPage += 1;
    await loadMembers();
  });
}

function bindPageSizeSelect() {
  const select = document.getElementById("memberPageSize");
  select?.addEventListener("change", async () => {
    pageSize = Number(select.value || 20);
    currentPage = 1;
    await loadMembers();
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

  return `
    <tr>
      <td>${escapeHtml(member.member_id)}</td>
      <td>${escapeHtml(member.email)}</td>
      <td>${escapeHtml(member.name)}</td>
      <td><span class="member-role">${escapeHtml(role)}</span></td>
      <td><span class="member-status ${statusClass(status)}">${escapeHtml(status)}</span></td>
      <td>${escapeHtml(formatDate(member.member_created_at))}</td>
      <td>${renderActionCell(member)}</td>
    </tr>
  `;
}

function renderActionCell(member) {
  if (member.role !== "USER") {
    return '<span class="member-action-empty">-</span>';
  }

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

function bindActionButtons(container) {
  container.querySelectorAll(".member-action-btn").forEach((button) => {
    button.addEventListener("click", async () => {
      await updateMemberStatus(button);
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
  } catch (error) {
    console.error(error);
    alert("회원 상태를 변경하지 못했습니다.");
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

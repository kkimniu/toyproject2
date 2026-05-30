import { apiRequest } from "../common/apiClient.js";

const contextPath = window.contextPath || "";
let currentType = "WORK_TYPE";
let categoryItems = [];
let currentPage = 1;
let pageSize = 10;
let keyword = "";

document.addEventListener("DOMContentLoaded", async () => {
  bindTabs();
  bindCategoryForm();
  bindCategorySearch();
  bindCategoryPagination();
  await loadCategories();
});

function bindTabs() {
  document.querySelectorAll(".category-tab").forEach((button) => {
    button.addEventListener("click", async () => {
      currentType = button.dataset.type || "WORK_TYPE";
      document.querySelectorAll(".category-tab").forEach((tab) => tab.classList.toggle("is-active", tab === button));
      const form = document.getElementById("categoryEditorForm");
      if (form?.elements.type) form.elements.type.value = currentType;
      resetCategoryForm(false);
      currentPage = 1;
      await loadCategories();
    });
  });
}

function bindCategoryForm() {
  const form = document.getElementById("categoryEditorForm");
  form?.addEventListener("submit", async (event) => {
    event.preventDefault();
    const formData = new FormData(form);
    const type = String(formData.get("type") || currentType);
    const id = String(formData.get("id") || "").trim();
    const name = String(formData.get("name") || "").trim();
    if (!name) return;
    const url = id
      ? `${contextPath}/api/admin/categories/${encodeURIComponent(id)}?type=${encodeURIComponent(type)}`
      : `${contextPath}/api/admin/categories?type=${encodeURIComponent(type)}`;
    const res = await apiRequest(url, {
      method: id ? "PATCH" : "POST",
      body: JSON.stringify({ name }),
    });
    if (!res.ok) {
      alert("카테고리를 저장하지 못했습니다. 이미 사용 중인 이름이거나 삭제할 수 없는 항목일 수 있습니다.");
      return;
    }
    currentType = type;
    resetCategoryForm(false);
    await loadCategories();
  });
  document.getElementById("btnResetCategoryForm")?.addEventListener("click", () => resetCategoryForm(false));
}

function bindCategorySearch() {
  document.getElementById("categorySearchForm")?.addEventListener("submit", (event) => {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    keyword = String(formData.get("keyword") || "").trim().toLowerCase();
    currentPage = 1;
    renderCategories();
  });
}

function bindCategoryPagination() {
  const pageSizeSelect = document.getElementById("categoryPageSize");
  pageSizeSelect?.addEventListener("change", () => {
    pageSize = Number(pageSizeSelect.value || 10);
    currentPage = 1;
    renderCategories();
  });
  document.getElementById("btnPrevCategories")?.addEventListener("click", () => {
    if (currentPage <= 1) return;
    currentPage -= 1;
    renderCategories();
  });
  document.getElementById("btnNextCategories")?.addEventListener("click", () => {
    const totalPages = getTotalPages();
    if (currentPage >= totalPages) return;
    currentPage += 1;
    renderCategories();
  });
}

async function loadCategories() {
  const tbody = document.getElementById("adminCategoryTableBody");
  const count = document.getElementById("adminCategoryCount");
  if (!tbody || !count) return;
  const res = await apiRequest(`${contextPath}/api/admin/categories?type=${encodeURIComponent(currentType)}`, { method: "GET" });
  let items = [];
  try {
    items = res.ok ? await res.json() : await loadPublicCategoriesFallback(res.status);
  } catch (error) {
    console.warn("category load failed:", error);
    tbody.innerHTML = '<tr class="data-table-empty"><td colspan="4">카테고리를 불러오지 못했습니다.</td></tr>';
    return;
  }
  categoryItems = Array.isArray(items) ? items : [];
  renderCategories();
}

function renderCategories() {
  const tbody = document.getElementById("adminCategoryTableBody");
  const count = document.getElementById("adminCategoryCount");
  const pageInfo = document.getElementById("categoryPageInfo");
  const prevButton = document.getElementById("btnPrevCategories");
  const nextButton = document.getElementById("btnNextCategories");
  if (!tbody || !count) return;

  const filteredItems = getFilteredItems();
  const totalPages = Math.max(1, Math.ceil(filteredItems.length / pageSize));
  currentPage = Math.min(Math.max(1, currentPage), totalPages);
  const start = (currentPage - 1) * pageSize;
  const pageItems = filteredItems.slice(start, start + pageSize);

  count.textContent = `${categoryTypeText(currentType)} ${filteredItems.length.toLocaleString("ko-KR")}개`;
  if (categoryItems.length === 0) {
    tbody.innerHTML = '<tr class="data-table-empty"><td colspan="4">카테고리가 없습니다.</td></tr>';
    updateCategoryPagination(1, 1, prevButton, nextButton, pageInfo);
    return;
  }
  if (filteredItems.length === 0) {
    tbody.innerHTML = '<tr class="data-table-empty"><td colspan="4">검색 결과가 없습니다.</td></tr>';
    updateCategoryPagination(1, 1, prevButton, nextButton, pageInfo);
    return;
  }
  tbody.innerHTML = pageItems.map(renderCategoryRow).join("");
  bindCategoryRowActions(tbody);
  updateCategoryPagination(currentPage, totalPages, prevButton, nextButton, pageInfo);
}

function getFilteredItems() {
  if (!keyword) return categoryItems;
  return categoryItems.filter((item) => String(item.name || "").toLowerCase().includes(keyword));
}

function getTotalPages() {
  return Math.max(1, Math.ceil(getFilteredItems().length / pageSize));
}

function updateCategoryPagination(page, totalPages, prevButton, nextButton, pageInfo) {
  if (pageInfo) pageInfo.textContent = `${page} / ${totalPages}`;
  if (prevButton) prevButton.disabled = page <= 1;
  if (nextButton) nextButton.disabled = page >= totalPages;
}

async function loadPublicCategoriesFallback(status) {
  console.warn(`admin category api failed: ${status}`);
  const res = await fetch(`${contextPath}/api/members/form-codes`, {
    method: "GET",
    credentials: "include",
  });
  if (!res.ok) {
    throw new Error(`category fallback failed: ${res.status}`);
  }
  const formCodes = await res.json();
  return normalizePublicCategories(formCodes, currentType);
}

function normalizePublicCategories(formCodes, type) {
  const config = {
    WORK_TYPE: ["work_types", "work_type_id", "work_type_name"],
    HOBBY: ["hobbies", "hobby_id", "hobby_name"],
    PREFERENCE: ["preferences", "preference_id", "preference_name"],
    PET: ["pets", "pet_id", "pet_name"],
  }[type];
  if (!config) return [];
  const [listKey, idKey, nameKey] = config;
  return (formCodes[listKey] || []).map((item) => ({
    type,
    id: item[idKey],
    name: item[nameKey],
    created_at: null,
  }));
}

function renderCategoryRow(item) {
  return `
    <tr>
      <td>${escapeHtml(item.id)}</td>
      <td>${escapeHtml(item.name)}</td>
      <td>${escapeHtml(formatDate(item.created_at))}</td>
      <td>
        <div class="member-actions">
          <button type="button" class="member-action-btn" data-edit-category-id="${escapeHtml(item.id)}" data-edit-category-name="${escapeAttribute(item.name)}">수정</button>
          <button type="button" class="member-action-btn member-delete-btn" data-delete-category-id="${escapeHtml(item.id)}">삭제</button>
        </div>
      </td>
    </tr>
  `;
}

function bindCategoryRowActions(container) {
  container.querySelectorAll("[data-edit-category-id]").forEach((button) => {
    button.addEventListener("click", () => {
      const form = document.getElementById("categoryEditorForm");
      if (!form) return;
      form.elements.type.value = currentType;
      form.elements.id.value = button.dataset.editCategoryId || "";
      form.elements.name.value = button.dataset.editCategoryName || "";
      form.scrollIntoView({ behavior: "smooth", block: "start" });
    });
  });
  container.querySelectorAll("[data-delete-category-id]").forEach((button) => {
    button.addEventListener("click", async () => {
      if (!confirm("카테고리를 삭제하시겠습니까? 이미 사용 중이면 삭제할 수 없습니다.")) return;
      const res = await apiRequest(`${contextPath}/api/admin/categories/${encodeURIComponent(button.dataset.deleteCategoryId)}?type=${encodeURIComponent(currentType)}`, {
        method: "DELETE",
      });
      if (!res.ok) {
        alert("카테고리를 삭제하지 못했습니다. 회원이 사용 중인 항목일 수 있습니다.");
        return;
      }
      await loadCategories();
    });
  });
}

function resetCategoryForm(clearType = true) {
  const form = document.getElementById("categoryEditorForm");
  form?.reset();
  if (form?.elements.id) form.elements.id.value = "";
  if (form?.elements.type) form.elements.type.value = clearType ? "WORK_TYPE" : currentType;
}

function categoryTypeText(type) {
  if (type === "WORK_TYPE") return "직업";
  if (type === "HOBBY") return "취미";
  if (type === "PREFERENCE") return "생활 선호";
  if (type === "PET") return "반려동물";
  return type;
}

function formatDate(value) {
  if (!value) return "-";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return String(value);
  return date.toLocaleDateString("ko-KR");
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function escapeAttribute(value) {
  return escapeHtml(value);
}

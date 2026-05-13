import { apiRequest } from "../common/apiClient.js";

const contextPath = window.contextPath || "";
const defaultProfileImage = `${contextPath}/resources/img/default-profile.svg`;
const roommateList = document.getElementById("recommendedRoommateList");
const searchForm = document.getElementById("mainSearchForm");
const filterGroups = document.getElementById("dynamicFilterGroups");
const filterSubmit = document.getElementById("filterSubmit");

document.addEventListener("DOMContentLoaded", () => {
  loadFormCodes();
  loadRecommendedRoommates();

  searchForm?.addEventListener("submit", (event) => {
    event.preventDefault();
    goToMapSearch(createCurrentFormData());
  });

  filterSubmit?.addEventListener("click", () => {
    loadRecommendedRoommates(createCurrentFormData());
  });

  roommateList?.addEventListener("click", handleRoommateActionClick);
});

async function loadRecommendedRoommates(formData = new FormData()) {
  if (!roommateList) return;

  const params = createSearchParams(formData);
  const query = params.toString();
  const url = `${contextPath}/api/members/recommended-roommates${query ? `?${query}` : ""}`;

  try {
    const response = await fetch(url, {
      method: "GET",
      headers: { Accept: "application/json" },
    });

    if (!response.ok) {
      throw new Error(`recommended roommates api failed: ${response.status}`);
    }

    renderRecommendedRoommates(await response.json());
  } catch (error) {
    console.error(error);
    roommateList.innerHTML = '<div class="roommate-empty">추천 룸메이트를 불러오지 못했습니다.</div>';
  }
}

async function loadFormCodes() {
  if (!filterGroups) return;

  try {
    const response = await fetch(`${contextPath}/api/members/form-codes`, {
      method: "GET",
      headers: { Accept: "application/json" },
    });

    if (!response.ok) {
      throw new Error(`form codes api failed: ${response.status}`);
    }

    renderFilterGroups(await response.json());
  } catch (error) {
    console.error(error);
    filterGroups.innerHTML = '<div class="filter-empty">필터를 불러오지 못했습니다.</div>';
  }
}

function renderFilterGroups(formCodes) {
  const groups = [
    { title: "직업", items: formCodes.work_types || formCodes.workTypes || [], idKey: "work_type_id", nameKey: "work_type_name" },
    { title: "취미", items: formCodes.hobbies || [], idKey: "hobby_id", nameKey: "hobby_name" },
    { title: "생활 선호", items: formCodes.preferences || [], idKey: "preference_id", nameKey: "preference_name" },
    { title: "반려동물", items: formCodes.pets || [], idKey: "pet_id", nameKey: "pet_name" },
  ];

  const html = groups
    .filter((group) => Array.isArray(group.items) && group.items.length > 0)
    .map(createFilterGroup)
    .join("");

  filterGroups.innerHTML = html || '<div class="filter-empty">등록된 필터가 없습니다.</div>';
}

function createFilterGroup(group) {
  const items = group.items
    .map((item) => {
      const id = item[group.idKey] ?? item[toCamelCase(group.idKey)];
      const name = item[group.nameKey] ?? item[toCamelCase(group.nameKey)];
      return `
        <label>
          <input type="checkbox" name="${escapeAttribute(group.idKey)}" value="${escapeAttribute(id)}">
          ${escapeHtml(name)}
        </label>
      `;
    })
    .join("");

  return `
    <div class="filter-group">
      <h3>${escapeHtml(group.title)}</h3>
      ${items}
    </div>
  `;
}

function renderRecommendedRoommates(roommates) {
  if (!Array.isArray(roommates) || roommates.length === 0) {
    roommateList.innerHTML = '<div class="roommate-empty">조건에 맞는 추천 룸메이트가 없습니다.</div>';
    return;
  }

  roommateList.innerHTML = roommates.map(createRoommateCard).join("");
}

function createRoommateCard(roommate) {
  const tags = Array.isArray(roommate.tags) ? roommate.tags.slice(0, 3) : [];
  const extraCount = Array.isArray(roommate.tags) && roommate.tags.length > 3 ? roommate.tags.length - 3 : 0;
  const tagHtml = [
    ...tags.map((tag) => `<span>${escapeHtml(tag)}</span>`),
    extraCount > 0 ? `<span>+${extraCount}</span>` : "",
  ].join("");

  const memberId = roommate.member_id ?? roommate.memberId;
  const roomId = roommate.room_id ?? roommate.roomId;
  const name = roommate.name || "이름 미등록";
  const ageText = roommate.age ? `, ${escapeHtml(String(roommate.age))}세` : "";
  const imageUrl = roommate.image_url ?? roommate.imageUrl ?? defaultProfileImage;
  const profileUrl = `${contextPath}/members/${encodeURIComponent(memberId)}`;
  const intro = roommate.intro || "등록된 프로필 정보를 기반으로 추천된 회원입니다.";

  return `
    <article class="roommate-card">
      <div class="card-image">
        <img src="${escapeAttribute(imageUrl)}" alt="${escapeAttribute(name)} 프로필">
      </div>
      <div class="card-body">
        <div class="card-title-row">
          <h3>${escapeHtml(name)}${ageText}</h3>
        </div>
        <p class="location">${escapeHtml(roommate.location || "지역 미등록")}</p>
        <p class="intro">${escapeHtml(intro)}</p>
        <p class="budget">${escapeHtml(roommate.budget || "예산 미등록")}</p>
        <div class="tag-list">${tagHtml}</div>
        <div class="card-actions">
          <a href="${profileUrl}" class="btn-outline">프로필 보기</a>
          <button type="button" class="btn-primary btn-message" data-room-id="${escapeAttribute(roomId)}" data-partner-id="${escapeAttribute(memberId)}">
            메시지
          </button>
        </div>
      </div>
    </article>
  `;
}

async function handleRoommateActionClick(event) {
  const messageButton = event.target.closest(".btn-message");
  if (!messageButton) return;

  const roomId = messageButton.dataset.roomId;
  const partnerId = messageButton.dataset.partnerId;
  if (!roomId || !partnerId) return;

  messageButton.disabled = true;
  try {
    const response = await apiRequest(`${contextPath}/api/chat/rooms`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
      },
      body: JSON.stringify({
        room_id: Number(roomId),
        partner_id: Number(partnerId),
      }),
    });

    if (response.status === 401 || response.status === 403) {
      window.openAuthModal?.("login");
      return;
    }
    if (!response.ok) {
      throw new Error(`chat room api failed: ${response.status}`);
    }

    const data = await response.json();
    const chatRoomId = data.chat_room_id ?? data.chatRoomId;
    if (chatRoomId) {
      window.location.href = `${contextPath}/chat/rooms/${encodeURIComponent(chatRoomId)}`;
    }
  } catch (error) {
    console.error(error);
    alert("채팅방을 열지 못했습니다.");
  } finally {
    messageButton.disabled = false;
  }
}

function createSearchParams(formData) {
  const params = new URLSearchParams();
  const region = formData.get("region");
  const budget = formData.get("budget");
  const gender = formData.get("gender");

  if (region) params.set("region", String(region).trim());
  if (budget) params.set("budget", budget);
  if (gender) params.set("gender", gender);

  appendSelectedValues(params, formData, "work_type_id");
  appendSelectedValues(params, formData, "hobby_id");
  appendSelectedValues(params, formData, "preference_id");
  appendSelectedValues(params, formData, "pet_id");

  return params;
}

function appendSelectedValues(params, formData, name) {
  formData.getAll(name)
    .filter((value) => value !== null && String(value).trim() !== "")
    .forEach((value) => params.append(name, value));
}

function goToMapSearch(formData) {
  const query = createSearchParams(formData).toString();
  window.location.href = `${contextPath}/rooms/map${query ? `?${query}` : ""}`;
}

function createCurrentFormData() {
  const formData = searchForm ? new FormData(searchForm) : new FormData();
  filterGroups?.querySelectorAll("input[type='checkbox']:checked").forEach((checkbox) => {
    formData.append(checkbox.name, checkbox.value);
  });
  return formData;
}

function toCamelCase(value) {
  return String(value).replace(/_([a-z])/g, (_, letter) => letter.toUpperCase());
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function escapeAttribute(value) {
  return escapeHtml(value).replaceAll("`", "&#096;");
}

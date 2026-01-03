// /resources/js/member/profileView.js
import { requireLogin } from "../common/authGuard.js";
import { apiRequest } from "../common/apiClient.js";
import { clearTokens } from "../common/authTokenStorage.js";

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function formatMoney(value) {
  if (value == null || value === "") return "-";
  const num = Number(value);
  if (Number.isNaN(num)) return String(value);
  return num.toLocaleString("ko-KR");
}

function statusText(status) {
  const s = String(status ?? "").toUpperCase();
  if (s === "OPEN") return "모집중";
  if (s === "RESERVED") return "예약중";
  if (s === "CLOSED") return "마감";
  if (s === "HIDDEN") return "비공개";
  return s || "";
}

function setText(id, text) {
  const el = document.getElementById(id);
  if (!el) return;
  el.textContent = text ?? "";
}

function pick(obj, keys) {
  for (const k of keys) {
    if (obj && obj[k] != null) return obj[k];
  }
  return undefined;
}

function itemLabel(item, fallback = "") {
  if (item == null) return "";
  if (typeof item === "string") return item;

  return (
    pick(item, ["name"]) ||
    pick(item, ["hobbyName", "hobby_name"]) ||
    pick(item, ["preferenceName", "preference_name"]) ||
    pick(item, ["petName", "pet_name"]) ||
    fallback
  );
}

function renderChips(containerId, items) {
  const container = document.getElementById(containerId);
  if (!container) return;

  container.innerHTML = "";

  const labels = (items || [])
    .map((it) => itemLabel(it))
    .map((v) => String(v).trim())
    .filter(Boolean);

  if (labels.length === 0) {
    const empty = document.createElement("span");
    empty.className = "chip-empty";
    empty.textContent = "없음";
    container.appendChild(empty);
    return;
  }

  labels.forEach((label) => {
    const chip = document.createElement("span");
    chip.className = "chip";
    chip.textContent = label;
    container.appendChild(chip);
  });
}

function formatJoinedAt(value) {
  if (!value) return "";
  if (typeof value === "string" && value.includes(".")) return value;

  if (typeof value === "string" && value.includes("-")) {
    const datePart = value.split("T")[0];
    return datePart.replaceAll("-", ". ");
  }

  if (typeof value === "number") {
    const d = new Date(value);
    if (!Number.isNaN(d.getTime())) {
      const y = d.getFullYear();
      const m = String(d.getMonth() + 1).padStart(2, "0");
      const day = String(d.getDate()).padStart(2, "0");
      return `${y}. ${m}. ${day}`;
    }
  }

  return String(value);
}

function initTabs() {
  const tabs = document.querySelectorAll(".mypage-tab");
  const contents = document.querySelectorAll(".mypage-tab-content");
  if (!tabs.length || !contents.length) return;

  function openTab(tabName) {
    contents.forEach((section) => {
      section.style.display = section.id === `tab-${tabName}` ? "block" : "none";
    });
    tabs.forEach((btn) => {
      btn.classList.toggle("active", btn.dataset.tab === tabName);
    });
  }

  tabs.forEach((btn) => {
    btn.addEventListener("click", () => openTab(btn.dataset.tab));
  });

  openTab(tabs[0].dataset.tab);
}

function renderMemberRoomCard(room) {
  const roomId = pick(room, ["roomId", "room_id"]);
  const title = pick(room, ["title"]) ?? "방 제목";
  const address = pick(room, ["address"]) ?? "";
  const thumb =
    pick(room, ["thumbnailUrl", "thumbnail_url", "thumbnail"]) ||
    "/resources/img/default-room.png";

  const deposit = pick(room, ["deposit", "securityDeposit", "security_deposit"]);
  const monthly = pick(room, ["monthlyRent", "monthly_rent", "monthly"]);
  const priceText =
    deposit != null || monthly != null
      ? `보증금 ${formatMoney(deposit)} / 월세 ${formatMoney(monthly)}`
      : pick(room, ["price"]) ?? "";

  const status = String(pick(room, ["status", "roomStatus", "room_status"]) ?? "").toUpperCase();

  return `
    <article class="my-post-card" data-room-id="${escapeHtml(roomId)}">
      <img class="my-post-thumb" src="${escapeHtml(thumb)}" alt="thumbnail">
      <div class="my-post-body">
        <p class="my-post-title">${escapeHtml(title)}</p>
        <p class="my-post-sub">${escapeHtml(address)}</p>
        ${priceText ? `<p class="my-post-price">${escapeHtml(priceText)}</p>` : ``}
      </div>
      ${status ? `<span class="my-post-status ${escapeHtml(status)}">${escapeHtml(statusText(status))}</span>` : ``}
    </article>
  `;
}

async function loadMemberProfile(memberId) {
  const profileRes = await apiRequest(`/api/members/${encodeURIComponent(memberId)}`, { method: "GET" });

  if (profileRes.status === 401 || profileRes.status === 403) {
    clearTokens();
    window.openAuthModal?.("login");
    return;
  }

  if (!profileRes.ok) {
    alert("프로필을 불러오지 못했습니다.");
    return;
  }

  const profile = await profileRes.json();

  const name = pick(profile, ["name", "nickname"]) ?? "사용자";
  setText("profileName", name);

  const labelEl = document.getElementById("profileLabel");
  if (labelEl) labelEl.textContent = `${name}님 프로필`;

  const photoEl = document.getElementById("profilePhoto");
  if (photoEl) {
    const photoUrl = pick(profile, ["photo_url", "photoUrl"]);
    photoEl.src = photoUrl || "/resources/img/default-profile.png";
  }

  const joinedAt = pick(profile, ["joined_at", "joinedAt"]);
  const joinedAtText = formatJoinedAt(joinedAt);
  if (joinedAtText) setText("profileJoinedAt", `가입일: ${joinedAtText}`);

  // 태그들
  // workType / mbti
  setText("profileWorkType", pick(profile, ["work_type_name" , "workTypeName"] )|| "직업/라이프스타일 미설정");
  setText("profileMbti", pick(profile, ["mbti"]) || "MBTI 미설정");

  // 흡연
  const smokingRaw = String(pick(profile, ["smoking"]) || "");
  setText("profileSmoking", smokingRaw === "SMOKER" ? "흡연" : smokingRaw === "NON_SMOKER" ? "비흡연" : "흡연 미설정");

  // 음주
  const drinkingRaw =  String(pick(profile, ["drinking"]) || "");
  setText("profileDrinking", drinkingRaw === "NONE" ? "음주 안함" : drinkingRaw === "SOCIAL" ? "가끔 마심" : drinkingRaw === "OFTEN" ? "자주 마심" : "음주 미설정");

  // 수면
  const sleepRaw =  pick(profile, ["sleep_time", "sleepTime"]);
  setText("profileSleepTime", sleepRaw === "EARLY" ? "일찍 잠 (22시 이전)" : sleepRaw === "NORMAL" ? "보통 (22~24시)" : sleepRaw === "LATE" ? "늦게 잠 (자정 이후)" : "수면 시간 미설정" );

  // 칩(객체 배열 대응)
  renderChips("profileHobbies", pick(profile, ["hobbies"]) ?? []);
  renderChips("profilePreferences", pick(profile, ["preferences"]) ?? []);
  renderChips("profilePets", pick(profile, ["pets"]) ?? []);
}

async function loadMemberRooms(memberId) {
  const container = document.getElementById("userRoomList");
  if (!container) return;

  const roomRes = await apiRequest(`/api/rooms/member/${encodeURIComponent(memberId)}`, { method: "GET" });

  if (!roomRes.ok) {
    container.innerHTML = `<p class="favorite-empty">등록한 방이 없습니다.</p>`;
    return;
  }

  const rooms = await roomRes.json();

  if (!Array.isArray(rooms) || rooms.length === 0) {
    container.innerHTML = `<p class="favorite-empty">등록한 방이 없습니다.</p>`;
    return;
  }

  container.innerHTML = rooms.map(renderMemberRoomCard).join("");

  container.querySelectorAll(".my-post-card").forEach((card) => {
    card.addEventListener("click", () => {
      const roomId = card.dataset.roomId;
      if (!roomId) return;

      window.location.href = `/rooms/${encodeURIComponent(roomId)}`;
    });
  });
}

document.addEventListener("DOMContentLoaded", async () => {
  if (!requireLogin()) return;

  initTabs();

  const root = document.getElementById("profilePage");
  const memberId = root?.dataset?.memberId;

  if (!memberId) {
    console.warn("profilePage의 data-member-id가 없습니다.");
    return;
  }

  try {
    await loadMemberProfile(memberId);
    await loadMemberRooms(memberId);
  } catch (e) {
    console.warn("profileView error:", e);
  }
});

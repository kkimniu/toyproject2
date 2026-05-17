import { apiRequest } from "../common/apiClient.js";
import { requireLogin } from "../common/authGuard.js";

const DEFAULT_PROFILE_IMAGE = "/resources/img/default-profile.svg";
const DEFAULT_ROOM_IMAGE = "/resources/img/default-room.svg";

document.addEventListener("DOMContentLoaded", async () => {
  if (!requireLogin()) return;

  initTabs();
  setupPhotoUpload();
  setupPasswordModal();
  setupPasswordChange();
  bindMyRoomActions();

  try {
    await Promise.all([
      loadMyProfile(),
      loadMyFavorites(),
      loadMyRooms(),
      loadMyReports(),
    ]);
  } catch (e) {
    console.error("mypage view init error:", e);
  }
});

async function loadMyProfile() {
  const res = await apiRequest("/api/members/me", { method: "GET" });
  if (!res.ok) throw new Error("failed to load /api/members/me: " + res.status);

  const data = await res.json();

  setText("profileName", data.name || "이름 미등록");
  setText("profileEmail", data.email || "이메일 미등록");
  setText("profilePhone", data.phone || "전화번호 미등록");
  setText("profileWorkType", data.work_type_name || data.workTypeName || "직업/라이프스타일 미설정");
  setText("profileMbti", data.mbti || "MBTI 미설정");
  setText("profileSmoking", smokingText(data.smoking));
  setText("profileDrinking", drinkingText(data.drinking));
  setText("profileSleepTime", sleepTimeText(data.sleep_time ?? data.sleepTime));

  const photo = document.getElementById("profilePhoto");
  if (photo) {
    photo.src = normalizeImageUrl(data.photo_url ?? data.photoUrl, DEFAULT_PROFILE_IMAGE);
    photo.onerror = () => {
      photo.onerror = null;
      photo.src = DEFAULT_PROFILE_IMAGE;
    };
  }

  const joinedAt = data.member_created_at ?? data.memberCreatedAt;
  setText("profileJoinedAt", joinedAt ? `가입일: ${formatDate(joinedAt)}` : "");

  renderChips(document.getElementById("profileHobbies"), data.hobbies || [], "hobby_name", "취미 없음");
  renderChips(document.getElementById("profilePreferences"), data.preferences || [], "preference_name", "생활 선호 없음");
  renderChips(document.getElementById("profilePets"), data.pets || [], "pet_name", "반려동물 조건 없음");
}

function renderChips(container, items, labelKey, emptyText) {
  if (!container) return;
  container.innerHTML = "";

  if (!Array.isArray(items) || items.length === 0) {
    const span = document.createElement("span");
    span.className = "chip-empty";
    span.textContent = emptyText;
    container.appendChild(span);
    return;
  }

  items.forEach((item) => {
    const label = item?.[labelKey] ?? item?.name ?? item;
    if (!label) return;
    const chip = document.createElement("span");
    chip.className = "chip";
    chip.textContent = label;
    container.appendChild(chip);
  });
}

async function loadMyFavorites() {
  const container = document.getElementById("favoriteList");
  if (!container) return;

  container.innerHTML = '<p class="favorite-empty">관심 목록을 불러오는 중입니다.</p>';

  try {
    const res = await apiRequest("/api/favorites/me", { method: "GET" });
    if (!res.ok) throw new Error("favorites load failed: " + res.status);
    renderFavoriteList(await res.json());
  } catch (e) {
    console.error("loadMyFavorites error:", e);
    container.innerHTML = '<p class="favorite-empty">관심 목록을 불러오지 못했습니다.</p>';
  }
}

function renderFavoriteList(list) {
  const container = document.getElementById("favoriteList");
  if (!container) return;

  if (!Array.isArray(list) || list.length === 0) {
    container.innerHTML = '<p class="favorite-empty">아직 관심 등록한 방이 없습니다.</p>';
    return;
  }

  container.innerHTML = list.map((room) => {
    const roomId = room.roomId ?? room.room_id;
    const title = room.roomTitle ?? room.room_title ?? room.title ?? "제목 없음";
    const address = room.address ?? "";
    const deposit = room.deposit;
    const monthlyRent = room.monthlyRent ?? room.monthly_rent;
    const status = room.status ?? "OPEN";
    const thumb = normalizeImageUrl(room.thumbnailUrl ?? room.thumbnail_url, DEFAULT_ROOM_IMAGE);

    return `
      <article class="my-post-card favorite-card" data-room-id="${escapeAttribute(roomId)}">
        <img class="my-post-thumb" src="${escapeAttribute(thumb)}" alt="방 이미지">
        <div class="my-post-body">
          <h4 class="my-post-title">${escapeHtml(title)}</h4>
          <p class="my-post-sub">${escapeHtml(address)}</p>
          <p class="my-post-price">보증금 ${formatMoney(deposit)} / 월세 ${formatMoney(monthlyRent)}</p>
        </div>
        <div class="my-post-status ${escapeAttribute(status)}">${statusText(status)}</div>
      </article>
    `;
  }).join("");
}

function setupPhotoUpload() {
  const input = document.getElementById("photoFileInput");
  const profilePhoto = document.getElementById("profilePhoto");
  if (!input) return;

  input.addEventListener("change", async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const formData = new FormData();
    formData.append("file", file);

    try {
      const res = await apiRequest("/api/members/me/photo", {
        method: "PUT",
        body: formData,
      });
      if (!res.ok) throw new Error("photo upload failed: " + res.status);

      const data = await res.json();
      const url = normalizeImageUrl(data.photo_url ?? data.photoUrl, DEFAULT_PROFILE_IMAGE);
      if (profilePhoto) profilePhoto.src = url;
      alert("프로필 사진이 변경되었습니다.");
    } catch (err) {
      console.error("photo upload error:", err);
      alert("프로필 사진 업로드에 실패했습니다.");
    } finally {
      input.value = "";
    }
  });
}

function initTabs() {
  const tabs = document.querySelectorAll(".mypage-tab");
  const contents = {
    favorites: document.getElementById("tab-favorites"),
    posts: document.getElementById("tab-posts"),
    activities: document.getElementById("tab-activities"),
    account: document.getElementById("tab-account"),
  };

  tabs.forEach((tab) => {
    tab.addEventListener("click", () => {
      const key = tab.dataset.tab;
      tabs.forEach((item) => item.classList.remove("active"));
      tab.classList.add("active");
      Object.entries(contents).forEach(([name, el]) => {
        if (el) el.style.display = name === key ? "block" : "none";
      });
    });
  });
}

function setupPasswordModal() {
  const openBtn = document.getElementById("btnOpenPasswordModal");
  const overlay = document.getElementById("passwordModalOverlay");
  const cancelBtn = document.getElementById("btnCancelPasswordModal");
  if (!openBtn || !overlay) return;

  const close = () => {
    overlay.style.display = "none";
  };

  openBtn.addEventListener("click", () => {
    ["currentPassword", "newPassword", "confirmPassword"].forEach((id) => {
      const el = document.getElementById(id);
      if (el) el.value = "";
    });
    setPasswordMessage("");
    overlay.style.display = "flex";
  });
  cancelBtn?.addEventListener("click", close);
  overlay.addEventListener("click", (e) => {
    if (e.target === overlay) close();
  });
}

function setupPasswordChange() {
  document.getElementById("btnChangePassword")?.addEventListener("click", handlePasswordChange);
}

async function handlePasswordChange() {
  const currentPassword = document.getElementById("currentPassword")?.value.trim() ?? "";
  const newPassword = document.getElementById("newPassword")?.value.trim() ?? "";
  const confirmPassword = document.getElementById("confirmPassword")?.value.trim() ?? "";

  if (!currentPassword || !newPassword || !confirmPassword) {
    alert("모든 비밀번호 항목을 입력해 주세요.");
    return;
  }
  if (newPassword.length < 8) {
    alert("새 비밀번호는 8자 이상이어야 합니다.");
    return;
  }
  if (newPassword !== confirmPassword) {
    alert("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
    return;
  }

  try {
    const res = await apiRequest("/api/members/me/password", {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        current_password: currentPassword,
        new_password: newPassword,
        confirm_password: confirmPassword,
      }),
    });

    if (!res.ok) {
      const error = await readError(res);
      setPasswordMessage(error || "비밀번호 변경에 실패했습니다.", "red");
      return;
    }

    setPasswordMessage("비밀번호가 변경되었습니다.", "green");
    setTimeout(() => {
      const overlay = document.getElementById("passwordModalOverlay");
      if (overlay) overlay.style.display = "none";
    }, 800);
  } catch (err) {
    console.error("password change error:", err);
    setPasswordMessage("서버 오류가 발생했습니다.", "red");
  }
}

async function loadMyRooms() {
  const wrap = document.getElementById("myRoomList");
  if (!wrap) return;

  wrap.innerHTML = '<p class="favorite-empty">내 게시글을 불러오는 중입니다.</p>';

  try {
    const res = await apiRequest("/api/rooms/me", { method: "GET" });
    if (!res.ok) throw new Error("my rooms load failed: " + res.status);

    const rooms = await res.json();
    if (!Array.isArray(rooms) || rooms.length === 0) {
      wrap.innerHTML = '<p class="favorite-empty">내가 등록한 방이 없습니다.</p>';
      return;
    }
    wrap.innerHTML = rooms.map(renderMyRoomCard).join("");
  } catch (e) {
    console.error("[mypage] loadMyRooms error:", e);
    wrap.innerHTML = '<p class="favorite-empty">내 게시글을 불러오지 못했습니다.</p>';
  }
}

async function loadMyReports() {
  const wrap = document.getElementById("myReportList");
  if (!wrap) return;

  wrap.innerHTML = '<p class="favorite-empty">신고 내역을 불러오는 중입니다.</p>';

  try {
    const res = await apiRequest("/api/reports/me", { method: "GET" });
    if (!res.ok) throw new Error("my reports load failed: " + res.status);

    const reports = await res.json();
    if (!Array.isArray(reports) || reports.length === 0) {
      wrap.innerHTML = '<p class="favorite-empty">접수한 신고가 없습니다.</p>';
      return;
    }

    wrap.innerHTML = reports.map(renderMyReportCard).join("");
  } catch (e) {
    console.error("[mypage] loadMyReports error:", e);
    wrap.innerHTML = '<p class="favorite-empty">신고 내역을 불러오지 못했습니다.</p>';
  }
}

function renderMyReportCard(report) {
  return `
    <article class="my-report-card">
      <div class="my-report-head">
        <div>
          <h4>${escapeHtml(report.target_member_name || "대상 회원")}</h4>
          <p>${escapeHtml(report.target_member_email || "-")}</p>
        </div>
        <span class="my-report-status ${reportStatusClass(report.status)}">${escapeHtml(reportStatusText(report.status))}</span>
      </div>
      <dl class="my-report-meta">
        <div>
          <dt>신고 사유</dt>
          <dd>${escapeHtml(report.reason || "-")}</dd>
        </div>
        <div>
          <dt>접수일</dt>
          <dd>${escapeHtml(formatDate(report.report_created_at))}</dd>
        </div>
        <div>
          <dt>처리 결과</dt>
          <dd>${escapeHtml(resolutionTypeText(report.resolution_type))}</dd>
        </div>
        <div>
          <dt>처리일</dt>
          <dd>${escapeHtml(report.processed_at ? formatDate(report.processed_at) : "-")}</dd>
        </div>
      </dl>
      <div class="my-report-message">
        ${escapeHtml(report.resolution_message || "아직 처리 결과가 등록되지 않았습니다.")}
      </div>
    </article>
  `;
}

function renderMyRoomCard(room) {
  const roomId = room.roomId ?? room.room_id;
  const title = room.roomTitle ?? room.room_title ?? room.title ?? "제목 없음";
  const address = room.address ?? "-";
  const status = room.status ?? "OPEN";
  const thumb = normalizeImageUrl(room.thumbnailUrl ?? room.thumbnail_url, DEFAULT_ROOM_IMAGE);
  const monthlyRent = room.monthlyRent ?? room.monthly_rent;
  const deposit = room.deposit;
  const priceText = monthlyRent != null || deposit != null
    ? `월세 ${formatMoney(monthlyRent)} / 보증금 ${formatMoney(deposit)}`
    : "가격 정보 없음";
  const createdAt = room.roomCreatedAt ?? room.room_created_at;

  return `
    <article class="my-post-card" data-room-id="${escapeAttribute(roomId)}">
      <img class="my-post-thumb" src="${escapeAttribute(thumb)}" alt="방 이미지">
      <div class="my-post-body">
        <h4 class="my-post-title">${escapeHtml(title)}</h4>
        <p class="my-post-sub">${escapeHtml(address)}</p>
        <p class="my-post-price">${escapeHtml(priceText)}</p>
        <div class="my-post-date">${createdAt ? formatDate(createdAt) : ""}</div>
      </div>
      <div class="my-post-status ${escapeAttribute(status)}">${statusText(status)}</div>
      <div class="my-post-actions">
        <button class="my-post-btn" data-action="edit" data-id="${escapeAttribute(roomId)}">수정</button>
        <button class="my-post-btn" data-action="toggle" data-id="${escapeAttribute(roomId)}">
          ${status === "OPEN" ? "모집 마감" : "모집 재개"}
        </button>
        <button class="my-post-btn" data-action="toggle-hidden" data-id="${escapeAttribute(roomId)}">
          ${status === "HIDDEN" ? "공개" : "비공개"}
        </button>
        <button class="my-post-btn-danger" data-action="delete" data-id="${escapeAttribute(roomId)}">삭제</button>
      </div>
    </article>
  `;
}

function bindMyRoomActions() {
  const wrap = document.getElementById("myRoomList");
  if (!wrap) return;

  wrap.addEventListener("click", async (e) => {
    const btn = e.target.closest("button[data-action]");
    if (btn) {
      e.preventDefault();
      e.stopPropagation();
      await handleRoomAction(btn);
      return;
    }

    const card = e.target.closest(".my-post-card");
    const roomId = card?.dataset.roomId;
    if (roomId) location.href = `/rooms/${encodeURIComponent(roomId)}`;
  });
}

async function handleRoomAction(btn) {
  const action = btn.dataset.action;
  const roomId = btn.dataset.id;
  if (!roomId) return;

  if (action === "edit") {
    location.href = `/rooms/${encodeURIComponent(roomId)}/edit`;
    return;
  }

  if (action === "delete") {
    if (!confirm("게시글을 삭제하면 더 이상 노출되지 않습니다.\n정말 삭제하시겠습니까?")) return;
    await requestRoomDelete(btn, roomId);
    return;
  }

  if (action === "toggle" || action === "toggle-hidden") {
    await requestRoomStatusChange(btn, roomId, action);
  }
}

async function requestRoomDelete(btn, roomId) {
  const wrap = document.getElementById("myRoomList");
  try {
    btn.disabled = true;
    const res = await apiRequest(`/api/rooms/${encodeURIComponent(roomId)}`, { method: "DELETE" });
    if (!res.ok) throw new Error("delete failed: " + res.status);
    btn.closest(".my-post-card")?.remove();
    if (wrap && wrap.querySelectorAll(".my-post-card").length === 0) {
      wrap.innerHTML = '<p class="favorite-empty">내가 등록한 방이 없습니다.</p>';
    }
  } catch (err) {
    console.error(err);
    alert("삭제 중 오류가 발생했습니다.");
  } finally {
    btn.disabled = false;
  }
}

async function requestRoomStatusChange(btn, roomId, action) {
  const card = btn.closest(".my-post-card");
  const statusEl = card?.querySelector(".my-post-status");
  if (!statusEl) return;

  const currentStatus = getStatusFromElement(statusEl);
  if (action === "toggle" && (currentStatus === "RESERVED" || currentStatus === "HIDDEN")) {
    alert("현재 상태에서는 모집 상태를 변경할 수 없습니다.");
    return;
  }

  const nextStatus = action === "toggle-hidden"
    ? (currentStatus === "HIDDEN" ? "OPEN" : "HIDDEN")
    : (currentStatus === "OPEN" ? "CLOSED" : "OPEN");

  try {
    btn.disabled = true;
    const res = await apiRequest(`/api/rooms/${encodeURIComponent(roomId)}/status`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ status: nextStatus }),
    });
    if (!res.ok) throw new Error("status update failed: " + res.status);

    statusEl.className = `my-post-status ${nextStatus}`;
    statusEl.textContent = statusText(nextStatus);
    updateActionButtons(card, nextStatus);
  } catch (err) {
    console.error(err);
    alert("상태 변경 중 오류가 발생했습니다.");
  } finally {
    btn.disabled = false;
  }
}

function updateActionButtons(card, status) {
  const toggleBtn = card?.querySelector('button[data-action="toggle"]');
  const hiddenBtn = card?.querySelector('button[data-action="toggle-hidden"]');
  if (toggleBtn) {
    toggleBtn.disabled = status === "HIDDEN";
    toggleBtn.textContent = status === "OPEN" ? "모집 마감" : "모집 재개";
  }
  if (hiddenBtn) hiddenBtn.textContent = status === "HIDDEN" ? "공개" : "비공개";
}

function getStatusFromElement(el) {
  return ["OPEN", "RESERVED", "CLOSED", "HIDDEN"].find((status) => el.classList.contains(status)) || "OPEN";
}

function setPasswordMessage(message, color = "") {
  const el = document.getElementById("passwordChangeMessage");
  if (!el) return;
  el.textContent = message;
  el.style.color = color;
}

async function readError(res) {
  try {
    const body = await res.json();
    return body?.message || body?.code || "";
  } catch (_) {
    return "";
  }
}

function setText(id, value) {
  const el = document.getElementById(id);
  if (el) el.textContent = value;
}

function smokingText(value) {
  if (value === "SMOKER") return "흡연";
  if (value === "NON_SMOKER") return "비흡연";
  return "흡연 여부 미설정";
}

function drinkingText(value) {
  if (value === "NONE") return "음주 안 함";
  if (value === "SOCIAL") return "가끔 마심";
  if (value === "OFTEN") return "자주 마심";
  return "음주 미설정";
}

function sleepTimeText(value) {
  if (value === "EARLY") return "일찍 잠";
  if (value === "NORMAL") return "보통";
  if (value === "LATE") return "늦게 잠";
  return "수면 시간 미설정";
}

function statusText(status) {
  switch (status) {
    case "OPEN": return "모집중";
    case "RESERVED": return "예약중";
    case "CLOSED": return "마감";
    case "HIDDEN": return "비공개";
    default: return "상태 미확인";
  }
}

function reportStatusClass(status) {
  if (status === "RESOLVED") return "RESOLVED";
  return "PENDING";
}

function reportStatusText(status) {
  if (status === "RESOLVED") return "처리완료";
  return "대기";
}

function resolutionTypeText(value) {
  if (value === "ACCEPTED") return "신고 인정";
  if (value === "REJECTED") return "신고 반려";
  if (value === "NO_ACTION") return "조치 없음";
  return "-";
}

function formatMoney(value) {
  if (value == null || value === "") return "-";
  const n = Number(value);
  if (Number.isNaN(n)) return "-";
  return n.toLocaleString("ko-KR");
}

function formatDate(dateStr) {
  const d = new Date(dateStr);
  if (Number.isNaN(d.getTime())) return "";
  return d.toLocaleDateString("ko-KR", { year: "numeric", month: "2-digit", day: "2-digit" });
}

function normalizeImageUrl(url, fallback) {
  if (!url) return fallback;
  if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("/")) return url;
  return `/${url}`;
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

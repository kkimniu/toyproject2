// /resources/js/member/mypageView.js
import { apiRequest } from "../common/apiClient.js";
import { requireLogin } from "../common/authGuard.js";

document.addEventListener("DOMContentLoaded", async () => {
  const ok = requireLogin();
  if (!ok) return;

  initTabs();
  setupPhotoUpload();
  setupPasswordModal();
  setupPasswordChange();

  try {
    await loadMyProfile();
    await loadMyFavorites();
  } catch (e) {
    console.error("mypage view init error:", e);
  }
});

/**
 * 내 프로필 카드 데이터 채우기
 */
async function loadMyProfile() {
  const res = await apiRequest("/api/members/me", { method: "GET" });
  if (!res.ok) {
    throw new Error("failed to load /api/members/me");
  }

  const data = await res.json();

  const profileName = document.getElementById("profileName");
  const profileEmail = document.getElementById("profileEmail");
  const profilePhone = document.getElementById("profilePhone");
  const profilePhoto = document.getElementById("profilePhoto");
  const profileWorkType = document.getElementById("profileWorkType");
  const profileMbti = document.getElementById("profileMbti");
  const profileSmoking = document.getElementById("profileSmoking");
  const profileDrinking = document.getElementById("profileDrinking");
  const profileSleepTime = document.getElementById("profileSleepTime");
  const profileHobbies = document.getElementById("profileHobbies");
  const profilePreferences = document.getElementById("profilePreferences");
  const profilePets = document.getElementById("profilePets");
  const profileJoinedAt = document.getElementById("profileJoinedAt");

  profileName && (profileName.textContent = data.name || "");
  profileEmail && (profileEmail.textContent = data.email || "");
  profilePhone && (profilePhone.textContent = data.phone || "");

  if (profilePhoto) {
    const url = data.photo_url || data.photoUrl || "";
    profilePhoto.src = url || "/resources/img/default-profile.png";
  }

  if (profileWorkType) {
    profileWorkType.textContent = data.work_type_name || "직업/라이프스타일 미설정";
  }

  if (profileMbti) {
    profileMbti.textContent = data.mbti || "MBTI 미설정";
  }

  if (profileSmoking) {
    profileSmoking.textContent = data.smoking === "SMOKER" ? "흡연" : "비흡연";
  }

  if (profileDrinking) {
    let drinkLabel = "음주 미설정";
    if (data.drinking === "NONE") drinkLabel = "음주 안함";
    if (data.drinking === "SOCIAL") drinkLabel = "가끔 마심";
    if (data.drinking === "OFTEN") drinkLabel = "자주 마심";
    profileDrinking.textContent = drinkLabel;
  }

  if (profileSleepTime) {
    const raw = data.sleep_time || data.sleepTime;
    let label = "수면 시간 미설정";

    if (raw === "EARLY") label = "일찍 잠 (22시 이전)";
    if (raw === "NORMAL") label = "보통 (22~24시)";
    if (raw === "LATE") label = "늦게 잠 (자정 이후)";

    profileSleepTime.textContent = label;
  }

  if (profileJoinedAt) {
    const createdRaw = data.member_created_at || data.memberCreatedAt;
    if (createdRaw) {
      const date = new Date(createdRaw);
      const text = `${date.getFullYear()}. ${date.getMonth() + 1}. ${date.getDate()}.`;
      profileJoinedAt.textContent = `가입일: ${text}`;
    } else {
      profileJoinedAt.textContent = "";
    }
  }

  // chip 리스트 렌더링
  renderChips(profileHobbies, data.hobbies || [], "hobby_name");
  renderChips(profilePreferences, data.preferences || [], "preference_name");
  renderChips(profilePets, data.pets || [], "pet_name");
}

function renderChips(container, items, labelKey) {
  if (!container) return;
  container.innerHTML = "";

  if (!Array.isArray(items) || items.length === 0) {
    const span = document.createElement("span");
    span.classList.add("chip-empty");
    span.textContent = "없음";
    container.appendChild(span);
    return;
  }

  items.forEach((item) => {
    const chip = document.createElement("span");
    chip.classList.add("chip");
    chip.textContent = item[labelKey];
    container.appendChild(chip);
  });
}
/**
 * 내가 찜한 방 목록 불러오기
 * GET /api/favorites/me
 */
async function loadMyFavorites() {
  const container = document.getElementById("favoriteList");
  if (!container) return;

  container.innerHTML = "<p>관심 목록을 불러오는 중입니다...</p>";

  try {
    const res = await apiRequest("/api/favorites/me", { method: "GET" });
    if (!res.ok) {
      container.innerHTML = "<p>관심 목록을 불러오지 못했습니다.</p>";
      return;
    }

    const list = await res.json();
    renderFavoriteList(list);
  } catch (e) {
    console.error("loadMyFavorites error:", e);
    container.innerHTML = "<p>관심 목록을 불러오지 못했습니다.</p>";
  }
}

function renderFavoriteList(list) {
  const container = document.getElementById("favoriteList");
  if (!container) return;

  if (!Array.isArray(list) || list.length === 0) {
    container.innerHTML = `<p class="favorite-empty">아직 관심 등록한 방이 없습니다.</p>`;
    return;
  }

  container.innerHTML = "";

  list.forEach((room) => {
    const roomId = room.roomId ?? room.room_id;
    const title = room.roomTitle ?? room.room_title ?? "제목 없음";
    const deposit = room.deposit;
    const monthlyRent = room.monthlyRent ?? room.monthly_rent;
    const thumbnail = room.thumbnailUrl ?? room.thumbnail_url;
    const depositMan = deposit != null ? Math.round(deposit / 10000) : null;
    const monthlyMan = monthlyRent != null ? Math.round(monthlyRent / 10000) : null;
    const depositText = depositMan != null ? `${depositMan.toLocaleString()}만` : "-";
    const monthlyText = monthlyMan != null ? `${monthlyMan.toLocaleString()}만` : "-";

    const status = room.status;
    let statusLabel = "";
    if (status === "OPEN") statusLabel = "모집중";
    else if (status === "RESERVED") statusLabel = "예약중";
    else if (status === "CLOSED") statusLabel = "마감";
    else if (status) statusLabel = status;

    const card = document.createElement("div");
    card.className = "favorite-card";

    card.innerHTML = `
      <div class="favorite-thumb">
        <img src="${thumbnail || '/resources/img/no-image.png'}" alt="thumbnail">
      </div>
      <div class="favorite-info">
        <h3 class="favorite-title">${title}</h3>
        <p class="favorite-meta">
          보증금 ${depositText} | 월세 ${monthlyText}
        </p>
        <span class="favorite-status">${statusLabel}</span>
      </div>
    `;

    card.addEventListener("click", () => {
      window.location.href = "/rooms/" + roomId;
    });

    container.appendChild(card);
  });
}

/**
 * 프로필 사진 업로드
 * -> PUT /api/members/me/photo (FormData)
 */
function setupPhotoUpload() {
  const input = document.getElementById("photoFileInput");
  const profilePhoto = document.getElementById("profilePhoto");

  if (!input) return;

  input.addEventListener("change", async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append("file", file);

    try {
      const res = await apiRequest("/api/members/me/photo", {
        method: "PUT",
        body: formData,
      });

      if (!res.ok) {
        alert("프로필 사진 업로드에 실패했습니다.");
        return;
      }

      const data = await res.json();
      const url = data.photo_url || data.photoUrl;

      if (profilePhoto && url) {
        profilePhoto.src = url;
      }

      alert("프로필 사진이 변경되었습니다.");
    } catch (err) {
      console.error("photo upload error:", err);
      alert("서버 오류가 발생했습니다.");
    } finally {
      input.value = "";
    }
  });
}

/**
 * 탭 전환 (관심 목록 / 내 게시글 / 활동 내역 / 계정 설정)
 */
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
      // active 클래스 변경
      tabs.forEach((t) => t.classList.remove("active"));
      tab.classList.add("active");

      // 컨텐츠 토글
      Object.entries(contents).forEach(([k, el]) => {
        if (!el) return;
        el.style.display = k === key ? "block" : "none";
      });
    });
  });
}

/**
 * 비밀번호 변경 모달 열기/닫기
 */
function setupPasswordModal() {
  const openBtn = document.getElementById("btnOpenPasswordModal");
  const overlay = document.getElementById("passwordModalOverlay");
  const cancelBtn = document.getElementById("btnCancelPasswordModal");

  if (!openBtn || !overlay) return;

  const open = () => {
    // 폼 초기화
    ["currentPassword", "newPassword", "confirmPassword"].forEach((id) => {
      const el = document.getElementById(id);
      if (el) el.value = "";
    });
    const msgEl = document.getElementById("passwordChangeMessage");
    if (msgEl) {
      msgEl.textContent = "";
      msgEl.style.color = "";
    }

    overlay.style.display = "flex";
  };

  const close = () => {
    overlay.style.display = "none";
  };

  openBtn.addEventListener("click", open);
  cancelBtn && cancelBtn.addEventListener("click", close);

  overlay.addEventListener("click", (e) => {
    if (e.target === overlay) {
      close();
    }
  });
}

/**
 * 비밀번호 변경 요청
 * -> PUT /api/members/me/password
 */
function setupPasswordChange() {
  const btn = document.getElementById("btnChangePassword");
  if (!btn) return;

  btn.addEventListener("click", handlePasswordChange);
}

async function handlePasswordChange() {
  const currentPasswordEl = document.getElementById("currentPassword");
  const newPasswordEl = document.getElementById("newPassword");
  const confirmPasswordEl = document.getElementById("confirmPassword");
  const messageEl = document.getElementById("passwordChangeMessage");
  const overlay = document.getElementById("passwordModalOverlay");

  const currentPassword = currentPasswordEl?.value.trim() ?? "";
  const newPassword = newPasswordEl?.value.trim() ?? "";
  const confirmPassword = confirmPasswordEl?.value.trim() ?? "";

  if (messageEl) {
    messageEl.textContent = "";
    messageEl.style.color = "";
  }

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

  const payload = {
    current_password: currentPassword,
    new_password: newPassword,
    confirm_password: confirmPassword,
  };

  try {
    const res = await apiRequest("/api/members/me/password", {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    if (!res.ok) {
      let msg = "비밀번호 변경에 실패했습니다.";
      try {
        const errBody = await res.json();
        if (errBody?.code === "INVALID_CURRENT_PASSWORD") {
          msg = "현재 비밀번호가 올바르지 않습니다.";
        } else if (errBody?.code === "PASSWORD_CONFIRM_MISMATCH") {
          msg = "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.";
        } else if (errBody?.code === "PASSWORD_SAME_AS_OLD") {
          msg = "기존 비밀번호와 동일한 비밀번호입니다.";
        } else if (errBody?.message) {
          msg = errBody.message;
        }
      } catch (_) {}

      if (messageEl) {
        messageEl.textContent = msg;
        messageEl.style.color = "red";
      } else {
        alert(msg);
      }
      return;
    }

    const data = await res.json();
    const msg = data?.message || "비밀번호가 변경되었습니다.";

    if (messageEl) {
      messageEl.textContent = msg;
      messageEl.style.color = "green";
    } else {
      alert(msg);
    }

    // 잠깐 보여주고 모달 닫기 (로그아웃 정책은 필요시 추가)
    setTimeout(() => {
      if (overlay) overlay.style.display = "none";
       window.location.href = "/";
    }, 1000);
  } catch (err) {
    console.error("password change error:", err);
    if (messageEl) {
      messageEl.textContent = "서버 오류가 발생했습니다.";
      messageEl.style.color = "red";
    } else {
      alert("서버 오류가 발생했습니다.");
    }
  }
}

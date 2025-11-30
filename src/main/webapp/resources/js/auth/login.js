// /resources/js/auth/login.js
// 로그인/회원가입 모달 전용 스크립트

import {
  saveTokens,
  clearTokens,
  getAccessToken,
  getTokenType,
} from "../common/authTokenStorage.js";

document.addEventListener("DOMContentLoaded", () => {
  setupTabs();
  setupLoginForm();
  setupRegisterForm();
  loadFormCodes();

  //헤더 버튼 로그인/로그아웃 상태 반영
  setupHeaderAuthButtons();

  // 전역에서 모달 열고 닫을 수 있게
  window.openAuthModal = openAuthModal;
  window.closeAuthModal = closeAuthModal;
});

function setupHeaderAuthButtons() {
  const loginBtn    = document.getElementById("btnOpenLogin");
  const registerBtn = document.getElementById("btnOpenRegister");
  const logoutBtn   = document.getElementById("btnLogout");

  // 헤더가 없는 페이지면 그냥 리턴
  if (!loginBtn || !logoutBtn) return;

  const hasToken = !!getAccessToken();

  if (hasToken) {
    // ✅ 로그인 상태 → 로그아웃만 보이게
    loginBtn.style.display = "none";
    if (registerBtn) registerBtn.style.display = "none";
    logoutBtn.style.display = "inline-block";
  } else {
    // ✅ 비로그인 상태 → 로그인 / 회원가입 표시
    loginBtn.style.display = "inline-block";
    if (registerBtn) registerBtn.style.display = "inline-block";
    logoutBtn.style.display = "none";
  }

  // ▶ 로그인 버튼: 로그인 탭을 열면서 모달 띄우기
  loginBtn.onclick = () => {
    openAuthModal();
    const loginTabBtn = document.querySelector('.auth-tab[data-tab="login"]');
    if (loginTabBtn) loginTabBtn.click();
  };

  // ▶ 회원가입 버튼: 회원가입 탭을 열면서 모달 띄우기
  if (registerBtn) {
    registerBtn.onclick = () => {
      openAuthModal();
      const regTabBtn = document.querySelector(
        '.auth-tab[data-tab="register"]'
      );
      if (regTabBtn) regTabBtn.click();
    };
  }

  // ▶ 로그아웃 버튼
  logoutBtn.onclick = async () => {
    try {
      const accessToken = getAccessToken();
      const tokenType = getTokenType() || "Bearer";

      if (accessToken) {
        await fetch("/api/auth/logout", {
          method: "POST",
          headers: {
            Authorization: `${tokenType} ${accessToken}`,
          },
        });
      }
    } catch (err) {
      console.error("logout error:", err);
    } finally {
      // 어쨌든 토큰은 지우고 새로고침
      clearTokens();
      location.reload();
    }
  };
}


/* =====================
 *  모달 열기/닫기
 * ===================== */
function openAuthModal() {
  const modal = document.getElementById("authModal");
  if (!modal) return;
  modal.classList.remove("hidden");
}

function closeAuthModal() {
  const modal = document.getElementById("authModal");
  if (!modal) return;
  modal.classList.add("hidden");
}

/* =====================
 *  탭 전환 (로그인 / 회원가입)
 * ===================== */
function setupTabs() {
  const tabs = document.querySelectorAll(".auth-tab");
  const loginTab = document.getElementById("loginTab");
  const registerTab = document.getElementById("registerTab");

  if (!tabs || !loginTab || !registerTab) return;

  tabs.forEach((tab) => {
    tab.addEventListener("click", () => {
      tabs.forEach((t) => t.classList.remove("active"));
      tab.classList.add("active");

      const target = tab.dataset.tab;
      if (target === "login") {
        loginTab.classList.remove("hidden");
        registerTab.classList.add("hidden");
      } else {
        loginTab.classList.add("hidden");
        registerTab.classList.remove("hidden");
      }
    });
  });
}

/* =====================
 *  로그인 처리
 * ===================== */
function setupLoginForm() {
  const loginForm = document.getElementById("loginForm");
  if (!loginForm) return;

  const loginEmail = document.getElementById("loginEmail");
  const loginPassword = document.getElementById("loginPassword");
  const loginError = document.getElementById("loginError");

  loginForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    loginError.textContent = "";

    const email = loginEmail.value.trim();
    const password = loginPassword.value;

    if (!email || !password) {
      loginError.textContent = "이메일과 비밀번호를 입력해주세요.";
      return;
    }

    try {
      // 기존 토큰 제거
      clearTokens();

      const res = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      if (!res.ok) {
        let message = "로그인에 실패했습니다. 이메일/비밀번호를 확인해주세요.";
        try {
          const errBody = await res.json();
          if (errBody && errBody.message) {
            message = errBody.message;
          }
        } catch (_) {}
        loginError.textContent = message;
        return;
      }

      const data = await res.json();
      const {
        access_token: accessToken,
        refresh_token: refreshToken,
        token_type: tokenType,
      } = data;

      if (!accessToken || !refreshToken) {
        loginError.textContent = "서버에서 토큰 정보를 받지 못했습니다.";
        console.error("Login response:", data);
        return;
      }

      // saveTokens가 객체를 받는지 / 개별 인자를 받는지 모를 때 안전하게 처리
      if (saveTokens.length >= 2) {
        // (accessToken, refreshToken, tokenType) 방식
        saveTokens(accessToken, refreshToken, tokenType || "Bearer");
      } else {
        // ({ accessToken, refreshToken, tokenType }) 방식
        saveTokens({
          accessToken,
          refreshToken,
          tokenType: tokenType || "Bearer",
        });
      }

      closeAuthModal();
      // 로그인 후 원하는 곳으로 이동
      // location.href = "/main";
      location.reload();
    } catch (err) {
      console.error("Login error:", err);
      loginError.textContent = "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
    }
  });
}

/* =====================
 *  회원가입 처리
 * ===================== */
function setupRegisterForm() {
  const registerForm = document.getElementById("registerForm");
  if (!registerForm) return;

  registerForm.addEventListener("submit", handleRegisterSubmit);
}

async function handleRegisterSubmit(e) {
  e.preventDefault();

  const name = document.getElementById("regName").value.trim();
  const email = document.getElementById("regEmail").value.trim();
  const phone = document.getElementById("regPhone").value.trim();
  const password = document.getElementById("regPassword").value;
  const confirm = document.getElementById("regConfirm").value;
  const smoking = document.getElementById("regSmoking").value;       // NON_SMOKER | SMOKER
  const drinking = document.getElementById("regDrinking").value;     // NONE | SOCIAL | OFTEN
  const sleepTime = document.getElementById("regSleepTime").value || null;
  const workTypeSelect = document.getElementById("regWorkType");
  const workTypeRaw = workTypeSelect.value;
  const mbti = document.getElementById("regMbti").value || null;
  const photoUrl = document.getElementById("regPhoto").value.trim();
  const registerError = document.getElementById("registerError");

  registerError.textContent = "";

  if (password !== confirm) {
    registerError.textContent = "비밀번호가 일치하지 않습니다.";
    return;
  }
  // 🔥 workTypeId 필수 체크 (DTO @NotNull)
  if (!workTypeRaw) {
    registerError.textContent = "직업/라이프스타일을 선택해주세요.";
    return;
  }
  const workTypeId = Number(workTypeRaw);

  // 체크된 취미/선호/반려동물 ID 수집
  const hobbyIds = Array.from(
    document.querySelectorAll(".hobby-checkbox:checked")
  ).map((el) => Number(el.value));

  const preferenceIds = Array.from(
    document.querySelectorAll(".preference-checkbox:checked")
  ).map((el) => Number(el.value));

  const petIds = Array.from(
    document.querySelectorAll(".pet-checkbox:checked")
  ).map((el) => Number(el.value));

  // ⚠️ 여기부터는 백엔드 SignUpRequest 필드 이름(camelCase)에 맞춘다
  const payload = {
    email,
    password,
    name,
    phone,

    // 자바: photoUrl  → JSON: photo_url
    photo_url: photoUrl || null,

    // 자바: sleepTime → JSON: sleep_time
    sleep_time: sleepTime,     // String
    // 자바: workTypeId → JSON: work_type_id
    work_type_id: workTypeId,  // Long or null
    smoking,                   // MemberSmokingEnum (NON_SMOKER / SMOKER)
    drinking,                  // MemberDrinkingEnum (NONE / SOCIAL / OFTEN)
    mbti,                      // String or null

    // 자바: hobbyIds → JSON: hobby_ids
    hobby_ids: hobbyIds,
    preference_ids: preferenceIds,
    pet_ids: petIds,
  };

  try {
    const res = await fetch("/api/auth/signup", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    if (!res.ok) {
      let message = "회원가입에 실패했습니다.";
      try {
        const errBody = await res.json();
        if (errBody && errBody.message) {
          message = errBody.message;
        }
      } catch (_) {}
      registerError.textContent = message;
      return;
    }

    alert("회원가입이 완료되었습니다. 로그인 해주세요.");

    // 탭을 로그인 쪽으로 이동
    const loginTabBtn = document.querySelector('.auth-tab[data-tab="login"]');
    if (loginTabBtn) loginTabBtn.click();
  } catch (err) {
    console.error("Register error:", err);
    registerError.textContent = "서버 오류가 발생했습니다.";
  }
}

/* =====================
 *  코드(직업/취미/선호/반려동물) 로딩
 * ===================== */
async function loadFormCodes() {
  const workTypeSelect = document.getElementById("regWorkType");
  const hobbyContainer = document.getElementById("hobbyCheckboxList");
  const prefContainer = document.getElementById("preferenceCheckboxList");
  const petContainer = document.getElementById("petCheckboxList");

  if (!workTypeSelect || !hobbyContainer || !prefContainer || !petContainer) {
    return;
  }

  try {
    const res = await fetch("/api/members/form-codes");
    if (!res.ok) {
      console.error("form-codes load failed");
      return;
    }

    const data = await res.json();
    // snake_case → JS 변수명으로 매핑
    const {
      work_types: workTypes,
      hobbies,
      preferences,
      pets,
    } = data;
    // 직업/라이프스타일
    if (Array.isArray(workTypes)) {
      workTypes.forEach((wt) => {
        const opt = document.createElement("option");
        opt.value = wt.work_type_id;
        opt.textContent = wt.work_type_name;
        workTypeSelect.appendChild(opt);
      });
    }

    // 취미
    if (Array.isArray(hobbies)) {
      hobbies.forEach((hobby) => {
        const label = document.createElement("label");
        label.classList.add("checkbox-item");

        const checkbox = document.createElement("input");
        checkbox.type = "checkbox";
        checkbox.classList.add("hobby-checkbox");
        checkbox.value = hobby.hobby_id;

        const span = document.createElement("span");
        span.textContent = hobby.hobby_name;

        label.appendChild(checkbox);
        label.appendChild(span);
        hobbyContainer.appendChild(label);
      });
    }

    // 생활 선호
    if (Array.isArray(preferences)) {
      preferences.forEach((pref) => {
        const label = document.createElement("label");
        label.classList.add("checkbox-item");

        const checkbox = document.createElement("input");
        checkbox.type = "checkbox";
        checkbox.classList.add("preference-checkbox");
        checkbox.value = pref.preference_id;

        const span = document.createElement("span");
        span.textContent = pref.preference_name;

        label.appendChild(checkbox);
        label.appendChild(span);
        prefContainer.appendChild(label);
      });
    }

    // 반려동물
    if (Array.isArray(pets)) {
      pets.forEach((pet) => {
        const label = document.createElement("label");
        label.classList.add("checkbox-item");

        const checkbox = document.createElement("input");
        checkbox.type = "checkbox";
        checkbox.classList.add("pet-checkbox");
        checkbox.value = pet.pet_id;

        const span = document.createElement("span");
        span.textContent = pet.pet_name;

        label.appendChild(checkbox);
        label.appendChild(span);
        petContainer.appendChild(label);
      });
    }
  } catch (err) {
    console.error("loadFormCodes error:", err);
  }
}

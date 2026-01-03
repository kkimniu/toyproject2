// /resources/js/auth/login.js
// 로그인/회원가입 모달 전용 스크립트
import {
  saveTokens,
  clearTokens,
  getAccessToken,
} from "../common/authTokenStorage.js";
import { apiRequest } from "../common/apiClient.js";

// ===== signup photo draft (사진만 복구) =====
const SIGNUP_KEY_KEY = "signupDraftKey";
const SIGNUP_DRAFT_VERSION = "v1";
const SIGNUP_PHOTO_TTL_MS = 24 * 60 * 60 * 1000;
// 네 실제 DOM id들 (여기만 맞추면 나머지 안 건드려도 됨)
const DOM = {
  signupKeyInputId: "regSignupKey",
  tempFileIdInputId: "regProfileTempFileId",
  previewImgId: "regProfilePhoto",
};
function signupPhotoKey(signupKey) {
  return `draft:auth:signupPhoto:${signupKey}:${SIGNUP_DRAFT_VERSION}`;
}

function getOrCreateSignupKey() {
  let key = localStorage.getItem(SIGNUP_KEY_KEY);
  if (!key) {
    key = crypto?.randomUUID?.() ?? `${Date.now()}-${Math.random()}`;
    localStorage.setItem(SIGNUP_KEY_KEY, key);
  }
  return key;
}

function saveSignupPhotoDraft({ signupKey, tempFileId, tempUrl }) {
  if (!signupKey || !tempFileId || !tempUrl) return;
  const payload = {
    signupKey,
    tempFileId,
    tempUrl,
    savedAt: Date.now(),
    ttlMs: SIGNUP_PHOTO_TTL_MS,
    path: location.pathname,
    v: SIGNUP_DRAFT_VERSION,
  };
  localStorage.setItem(signupPhotoKey(signupKey), JSON.stringify(payload));
}

function loadSignupPhotoDraft(signupKey) {
  try {
    if (!signupKey) return null;
    const raw = localStorage.getItem(signupPhotoKey(signupKey));
    if (!raw) return null;

    const data = JSON.parse(raw);
    if (!data?.signupKey || !data?.tempFileId || !data?.tempUrl) return null;

    if (data.signupKey !== signupKey) return null;
    if (data.v !== SIGNUP_DRAFT_VERSION) return null;
    if (data.path && data.path !== location.pathname) return null;

    const ttl = Number(data.ttlMs ?? 0);
    const savedAt = Number(data.savedAt ?? 0);
    if (!ttl || !savedAt) return null;
    if (Date.now() - savedAt > ttl) return null;

    return data;
  } catch {
    return null;
  }
}

function clearSignupPhotoDraft(signupKey) {
  if (!signupKey) return;
  localStorage.removeItem(signupPhotoKey(signupKey));
}

function clearSignupDraftAll() {
  const signupKey = localStorage.getItem(SIGNUP_KEY_KEY);
  if (signupKey) clearSignupPhotoDraft(signupKey);
  localStorage.removeItem(SIGNUP_KEY_KEY);
}

// 페이지 진입 시 accessToken 만료되어 있으면 refreshToken으로 자동 재발급 시도
async function syncAuthOnPageLoad() {
  const accessToken = getAccessToken();

  if (!accessToken) return;

  try {
    const res = await apiRequest("/api/members/me", { method: "GET" });
    if (!res.ok) {
      clearTokens();
    }
  } catch (err) {
    console.error("syncAuthOnPageLoad error:", err);
    // 에러가 나도 꼬인 토큰은 정리
    clearTokens();
  }
}

async function initSignupDraftPhotoUI() {
  // signup_key는 "항상 유지" (회원가입 폼 열 때마다 동일)
  const signupKey = getOrCreateSignupKey();
  const signupKeyInput = document.getElementById(DOM.signupKeyInputId);
  if (signupKeyInput) signupKeyInput.value = signupKey;

  // 사진 드래프트가 있으면 "물어보고" 복구
  const draft = loadSignupPhotoDraft(signupKey);
  if (!draft) return;

  const ok = confirm("이전에 업로드한 프로필 사진이 남아있습니다.\n불러오시겠습니까?");
  if (!ok) {
    try {
      await fetch(`/api/files/temp/profile-signup?temp_file_id=${encodeURIComponent(draft.tempFileId)}&signup_key=${encodeURIComponent(draft.signupKey)}`, {
        method: "DELETE",
      });
    } catch (e) {
      console.error("temp delete failed:", e);
    }
    clearSignupPhotoDraft(signupKey);

    const tempIdInput = document.getElementById(DOM.tempFileIdInputId);
    if (tempIdInput) tempIdInput.value = "";

    const preview = document.getElementById(DOM.previewImgId);
    if (preview) preview.src = "";

    return;
  }
  // 3) hidden tempFileId 복구
  const tempIdInput = document.getElementById(DOM.tempFileIdInputId);
  if (tempIdInput) tempIdInput.value = String(draft.tempFileId);

  // 4) 미리보기 복구
  const preview = document.getElementById(DOM.previewImgId);
  if (preview) preview.src = draft.tempUrl;

}

document.addEventListener("DOMContentLoaded", async () => {
  // 1) 우선 accessToken 이 만료돼 있으면 여기서 refresh 시도
  await syncAuthOnPageLoad();
   // 2) 그 다음부터는 항상 "최신 accessToken 기준"으로 로그인 상태 판단
  setupTabs();
  setupLoginForm();
  setupRegisterForm();
  loadFormCodes();
  // 업로드 기능은 항상 붙여두고,
  // draft 복구(confirm)는 register 탭 열릴 때만 실행
  setupRegisterPhotoUpload();
});



/* =====================
 *  모달 열기/닫기
 * ===================== */
function openAuthModal(defaultTab = "login") {
  const modal = document.getElementById("authModal");
  if (!modal) return;
  modal.classList.remove("hidden");
  // 탭/콘텐츠 DOM 찾기
  const loginTabBtn = document.querySelector('.auth-tab[data-tab="login"]');
  const registerTabBtn = document.querySelector('.auth-tab[data-tab="register"]');
  const loginTab = document.getElementById("loginTab");
  const registerTab = document.getElementById("registerTab");

  if (!loginTabBtn || !registerTabBtn || !loginTab || !registerTab) return;

  // 기본 탭 설정
  if (defaultTab === "register") {
    loginTabBtn.classList.remove("active");
    registerTabBtn.classList.add("active");
    loginTab.classList.add("hidden");
    registerTab.classList.remove("hidden");
    initSignupDraftPhotoUI();
  } else {
    // login
    loginTabBtn.classList.add("active");
    registerTabBtn.classList.remove("active");
    loginTab.classList.remove("hidden");
    registerTab.classList.add("hidden");
  }
}

function closeAuthModal() {
  const modal = document.getElementById("authModal");
  if (!modal) return;
  modal.classList.add("hidden");
}

window.openAuthModal = openAuthModal;
window.closeAuthModal = closeAuthModal;

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
        initSignupDraftPhotoUI();
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
    if (loginError) loginError.textContent = "";

    const email = loginEmail?.value?.trim() ?? "";
    const password = loginPassword?.value ?? "";

    if (!email || !password) {
      if (loginError) loginError.textContent = "이메일과 비밀번호를 입력해주세요.";
      return;
    }

    try {
      // 기존 토큰 제거
      clearTokens();

      const res = await fetch("/api/auth/login", {
        method: "POST",
        credentials: "include",
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
      const accessToken = data.access_token;
      const tokenType = data.token_type || "Bearer";

      if (!accessToken) {
        loginError.textContent = "서버에서 토큰 정보를 받지 못했습니다.";
        console.error("Login response:", data);
        return;
      }

      saveTokens({ accessToken, tokenType });

      closeAuthModal();
      location.reload();
    } catch (err) {
      console.error("Login error:", err);
      loginError.textContent = "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
    }
  });
}

/* =====================
 *  회원가입 - 프로필 사진 파일 선택 + 미리보기
 * ===================== */
function setupRegisterPhotoUpload() {
  const fileInput = document.getElementById("regPhotoFileInput");
  const uploadBtn = document.getElementById("btnRegPhotoUpload");
  const previewImg = document.getElementById("regProfilePhoto");
  const tempIdInput = document.getElementById("regProfileTempFileId");
  const signupKeyInput = document.getElementById("regSignupKey");

  // 요소 중 하나라도 없으면 안전하게 종료
  if (!fileInput || !uploadBtn || !previewImg || !tempIdInput || !signupKeyInput) return;

  // 버튼 클릭 → 숨겨진 file input 클릭
  uploadBtn.addEventListener("click", () => {
    fileInput.click();
  });

  // 파일 선택 → 이미지 미리보기
  fileInput.addEventListener("change", async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    // 간단 이미지 타입 체크 (선택사항)
    if (!file.type.startsWith("image/")) {
      alert("이미지 파일만 선택할 수 있습니다.");
      fileInput.value = "";
      return;
    }

    const signupKey = signupKeyInput.value || getOrCreateSignupKey();
    signupKeyInput.value = signupKey;
    if (!signupKey) {
      console.error("[SignupUpload] signupKey missing. Upload aborted.");
      return;
    }
    const formData = new FormData();
    formData.append("file", file);
    formData.append("signup_key", signupKey);

    try {
      const res = await fetch(`/api/files/temp/profile-signup`, {
        method: "PUT",
        body: formData,
      });

      if (!res.ok) {
        alert("프로필 이미지 업로드에 실패했습니다.");
        return;
      }

      const data = await res.json();
      // 백엔드 TempUploadFileResponse: temp_file_id, temp_url
      const tempFileId = data.temp_file_id ?? data.tempFileId;
      const tempUrl    = data.temp_url ?? data.tempPath ?? data.temp_path;

      // 1) hidden input에 temp_file_id 저장 → 회원가입 submit 때 같이 보냄
      tempIdInput.value = tempFileId;

      // 2) 미리보기 이미지 변경
      previewImg.src = tempUrl;

      // 3) 사진만 draft 저장 (정석)
      saveSignupPhotoDraft({ signupKey, tempFileId, tempUrl });
    } catch (err) {
      console.error("temp profile upload error:", err);
      alert("이미지 업로드 중 오류가 발생했습니다.");
    } finally {
      fileInput.value = "";
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

  const name = document.getElementById("regName")?.value?.trim() ?? "";
  const email = document.getElementById("regEmail")?.value?.trim() ?? "";
  const phone = document.getElementById("regPhone")?.value?.trim() ?? "";
  const password = document.getElementById("regPassword")?.value ?? "";
  const confirmPw = document.getElementById("regConfirm")?.value ?? "";
  const smoking = document.getElementById("regSmoking")?.value ?? "NON_SMOKER";
  const drinking = document.getElementById("regDrinking")?.value ?? "NONE";
  const sleepTime = document.getElementById("regSleepTime")?.value || null;
  const workTypeRaw = document.getElementById("regWorkType")?.value ?? "";
  const mbti = document.getElementById("regMbti")?.value || null;

  // 🔹 숨겨진 tempFileId
  const profileTempFileIdVal = document.getElementById(DOM.tempFileIdInputId)?.value ?? "";
  const profileTempFileId = profileTempFileIdVal ? Number(profileTempFileIdVal) : null;

  const signupKey = document.getElementById("regSignupKey")?.value || null;

  const registerError = document.getElementById("registerError");
  if (registerError) registerError.textContent = "";

  if (password !== confirmPw) {
    if (registerError) {
      registerError.textContent = "비밀번호가 일치하지 않습니다.";
    }
    return;
  }
  // workTypeId 필수 체크 (DTO @NotNull)
  if (!workTypeRaw) {
    if (registerError) {
    registerError.textContent = "직업/라이프스타일을 선택해주세요.";
    }
    return;
  }
  const workTypeId = Number(workTypeRaw);

  // 체크된 취미/선호/반려동물 ID 수집
  const hobbyIds = Array.from(document.querySelectorAll(".hobby-checkbox:checked")).map((el) => Number(el.value));

  const preferenceIds = Array.from(document.querySelectorAll(".preference-checkbox:checked")).map((el) => Number(el.value));

  const petIds = Array.from(document.querySelectorAll(".pet-checkbox:checked")).map((el) => Number(el.value));

  // 여기부터는 백엔드 SignUpRequest 필드 이름(camelCase)에 맞춘다
  const payload = {
    email,
    password,
    name,
    phone,

    // 자바: sleepTime → JSON: sleep_time
    sleep_time : sleepTime,     // String
    // 자바: workTypeId → JSON: work_type_id
    work_type_id : workTypeId,  // Long or null
    smoking,                   // MemberSmokingEnum (NON_SMOKER / SMOKER)
    drinking,                  // MemberDrinkingEnum (NONE / SOCIAL / OFTEN)
    mbti,                      // String or null

    // 자바: hobbyIds → JSON: hobby_ids
    hobby_ids : hobbyIds,
    preference_ids : preferenceIds,
    pet_ids: petIds,

    // 🔹 임시 파일 ID 전달 (SignUpRequest.profileTempFileId)
    profile_temp_file_id: profileTempFileId,
    signup_key: signupKey,

    photo_url: null,
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
      if (registerError) registerError.textContent = message;
      return;
    }

    alert("회원가입이 완료되었습니다. 로그인 해주세요.");

    clearSignupDraftAll();
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

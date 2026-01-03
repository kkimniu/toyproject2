// /resources/js/member/mypageEdit.js

import { apiRequest } from "../common/apiClient.js";
import { requireLogin } from "../common/authGuard.js";

document.addEventListener("DOMContentLoaded", async () => {
  // 1) 비로그인 → 로그인 모달 열고 종료
  const ok = requireLogin();
  if (!ok) {
    return;
  }

  try {
    // 2) 코드 먼저 로딩 → 셀렉트/체크박스 채우기
    await loadFormCodesForMypage();
    // 3) 내 프로필 정보 로딩 → 값 채우기
    await loadMyProfile();
    // 4) 폼 submit 이벤트 등록
    setupMypageFormSubmit();
  } catch (e) {
    console.error("mypage init error:", e);
    const errSpan = document.getElementById("mypageError");
    if (errSpan) {
      errSpan.textContent = "마이페이지 정보를 불러오는 중 오류가 발생했습니다.";
    }
  }
});
document.getElementById("photoFileInput")?.addEventListener("change",async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append("file", file);

    try {
        const res = await apiRequest("/api/members/me/photo", {
            method: "PUT",
            body: formData,
        });
        if (res.status === 401 || res.status === 403) {
          clearTokens();
          window.openAuthModal?.("login");
          return;
        }
        if(!res.ok) {
            alert("프로필 사진 업로드에 실패했습니다");
            return;
        }

        const data = await res.json();

        const profilePhoto = document.getElementById("profilePhoto");
        if (profilePhoto) {
            profilePhoto.src = data.photo_url || data.photoUrl || "/resources/img/default-profile.png" ;
        }

        alert("프로필 사진이 변경되었습니다.");
    } catch (err) {
      console.error("photo upload error:", err);
      alert("서버 오류가 발생했습니다.");
    }
});
/**
 * 직업/취미/선호/반려동물 코드 로딩해서 마이페이지 폼에 렌더링
 */
async function loadFormCodesForMypage() {
  const workTypeSelect = document.getElementById("mpWorkType");
  const hobbyContainer = document.getElementById("mpHobbyCheckboxList");
  const prefContainer = document.getElementById("mpPreferenceCheckboxList");
  const petContainer = document.getElementById("mpPetCheckboxList");

  if (!workTypeSelect || !hobbyContainer || !prefContainer || !petContainer) {
    return;
  }

  const res = await apiRequest("/api/members/form-codes", { method: "GET" });
  if (res.status === 401 || res.status === 403) {
    clearTokens();
    window.openAuthModal?.("login");
    throw new Error("AUTH");
  }
  if (!res.ok) {
    throw new Error("form-codes load failed");
  }

  const data = await res.json();
  const { work_types: workTypes, hobbies, preferences, pets } = data;

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
}

/**
 * /api/members/me 로 내 정보 조회해서
 *  - 왼쪽 프로필 카드
 *  - 오른쪽 수정 폼
 *   에 값 채워 넣기
 */
async function loadMyProfile() {
  const res = await apiRequest("/api/members/me", { method: "GET" });
  if (res.status === 401 || res.status === 403) {
    clearTokens();
    window.openAuthModal?.("login");
    throw new Error("AUTH");
  }
  if (!res.ok) {
    throw new Error("failed to load /api/members/me");
  }

  const data = await res.json();

  // ----- 왼쪽 프로필 카드 -----
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
    profileMbti.textContent = data.mbti ? `MBTI: ${data.mbti}` : "MBTI 미설정";
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

    if (raw === "EARLY")  label = "일찍 잠 (22시 이전)";
    if (raw === "NORMAL") label = "보통 (22~24시)";
    if (raw === "LATE")   label = "늦게 잠 (자정 이후)";

    profileSleepTime.textContent = label;
  }
  // chip list 렌더링 헬퍼
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

  renderChips(profileHobbies, data.hobbies || [], "hobby_name");
  renderChips(profilePreferences, data.preferences || [], "preference_name");
  renderChips(profilePets, data.pets || [], "pet_name");

  // ----- 오른쪽 수정 폼 값 세팅 -----
  const mpName = document.getElementById("mpName");
  const mpPhone = document.getElementById("mpPhone");
  const mpWorkType = document.getElementById("mpWorkType");
  const mpSleepTime = document.getElementById("mpSleepTime");
  const mpSmoking = document.getElementById("mpSmoking");
  const mpDrinking = document.getElementById("mpDrinking");
  const mpMbti = document.getElementById("mpMbti");

  mpName && (mpName.value = data.name || "");
  mpPhone && (mpPhone.value = data.phone || "");
  if (mpWorkType && (data.work_type_id || data.workTypeId)) {
    mpWorkType.value = data.work_type_id || data.workTypeId;
  }
  mpSleepTime &&
    (mpSleepTime.value = data.sleep_time || data.sleepTime || "");
  mpSmoking && (mpSmoking.value = data.smoking || "NON_SMOKER");
  mpDrinking && (mpDrinking.value = data.drinking || "NONE");
  mpMbti && (mpMbti.value = data.mbti || "");

  // 취미/선호/펫 체크박스 체크 상태 반영
  const hobbyIds = (data.hobbies || []).map((h) => h.hobby_id);
  const preferenceIds = (data.preferences || []).map(
    (p) => p.preference_id
  );
  const petIds = (data.pets || []).map((p) => p.pet_id);

  document
    .querySelectorAll("#mpHobbyCheckboxList .hobby-checkbox")
    .forEach((cb) => {
      const id = Number(cb.value);
      cb.checked = hobbyIds.includes(id);
    });

  document
    .querySelectorAll("#mpPreferenceCheckboxList .preference-checkbox")
    .forEach((cb) => {
      const id = Number(cb.value);
      cb.checked = preferenceIds.includes(id);
    });

  document
    .querySelectorAll("#mpPetCheckboxList .pet-checkbox")
    .forEach((cb) => {
      const id = Number(cb.value);
      cb.checked = petIds.includes(id);
    });
}

/**
 * 폼 submit → PUT /api/members/me
 */
function setupMypageFormSubmit() {
  const form = document.getElementById("mypageForm");
  const errorSpan = document.getElementById("mypageError");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    if (errorSpan) errorSpan.textContent = "";

    const name = document.getElementById("mpName").value.trim();
    const phone = document.getElementById("mpPhone").value.trim();
    const workTypeRaw = document.getElementById("mpWorkType").value;
    const sleepTime = document.getElementById("mpSleepTime").value || null;
    const smoking = document.getElementById("mpSmoking").value;
    const drinking = document.getElementById("mpDrinking").value;
    const mbti = document.getElementById("mpMbti").value || null;

    const hobbyIds = Array.from(
      document.querySelectorAll(
        "#mpHobbyCheckboxList .hobby-checkbox:checked"
      )
    ).map((el) => Number(el.value));
    const preferenceIds = Array.from(
      document.querySelectorAll(
        "#mpPreferenceCheckboxList .preference-checkbox:checked"
      )
    ).map((el) => Number(el.value));
    const petIds = Array.from(
      document.querySelectorAll(
        "#mpPetCheckboxList .pet-checkbox:checked"
      )
    ).map((el) => Number(el.value));

    const workTypeId = workTypeRaw ? Number(workTypeRaw) : null;

    // MemberProfileUpdateRequest + SnakeCase 기준 payload
    const payload = {
      name,
      phone,
      work_type_id: workTypeId,
      sleep_time: sleepTime,
      smoking,
      drinking,
      mbti,
      hobby_ids: hobbyIds,
      preference_ids: preferenceIds,
      pet_ids: petIds,
    };

    try {
      const res = await apiRequest("/api/members/me", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        let message = "프로필 수정에 실패했습니다.";
        try {
          const errBody = await res.json();
          if (errBody && errBody.message) {
            message = errBody.message;
          }
        } catch (_) {}
        if (errorSpan) errorSpan.textContent = message;
        return;
      }
        console.log("mbti value before send:", document.getElementById("mpMbti").value);
        console.log("payload:", payload);

      alert("프로필이 수정되었습니다.");
      // 수정 후 최신 데이터 다시 로딩
      await loadMyProfile();
    } catch (err) {
      console.error("update profile error:", err);
      if (errorSpan) {
        errorSpan.textContent = "서버 오류가 발생했습니다.";
      }
    }
  });
}
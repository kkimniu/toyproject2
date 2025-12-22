// ES Module
// /resources/js/room/roomCreate.js
import { apiRequest } from "../common/apiClient.js";
import { requireLogin } from "../common/authGuard.js";
import { makeUserScope, makeDraftKey, saveDraft, loadDraft, clearDraft } from "../common/draftStore.js";

let selectedFiles = [];

// ===== DraftStore keys =====
const USER_SCOPE = makeUserScope();
const APP = "roommate";
const PAGE = "roomCreate";
const VERSION = "v1";
const TTL_MS = 1000 * 60 * 60 * 24;

const TEXT_PAGE = `${PAGE}:text`;
const IMG_PAGE = `${PAGE}:images`;

const TEXT_KEY = makeDraftKey({ app: APP, page: TEXT_PAGE, userScope: USER_SCOPE, version: VERSION });
const IMG_KEY  = makeDraftKey({ app: APP, page: IMG_PAGE, userScope: USER_SCOPE, version: VERSION });

// ===== utils =====
function v(id) {
  return (document.getElementById(id)?.value ?? "").trim();
}
function n(id) {
  const x = v(id);
  return x === "" ? null : Number(x);
}
function i(id) {
  const x = v(id);
  return x === "" ? null : parseInt(x, 10);
}

function validate(payload) {
  if (!payload.title) return "제목은 필수입니다.";
  if (!payload.content) return "상세 설명은 필수입니다.";
  if (!payload.address) return "주소는 필수입니다.";
  if (payload.room_type_id == null || Number.isNaN(payload.room_type_id)) return "방 타입은 필수입니다.";

  const checks = [
    ["월세", payload.monthly_rent],
    ["보증금", payload.deposit],
  ];
  for (const [name, val] of checks) {
    if (val == null || Number.isNaN(val)) return `${name} 값을 입력해주세요.`;
    if (val < 0) return `${name} 값은 0 이상이어야 합니다.`;
  }
  if (payload.area_m2 != null && payload.area_m2 < 0) return "면적은 0 이상이어야 합니다.";
  if (payload.floor != null && payload.floor < 0) return "층수는 0 이상이어야 합니다.";
  if (payload.max_roommates != null && payload.max_roommates < 0) return "최대 룸메이트는 0 이상이어야 합니다.";
  return null;
}

// ===== debounce =====
function debounce(fn, wait = 250) {
  let t = null;
  return (...args) => {
    clearTimeout(t);
    t = setTimeout(() => fn(...args), wait);
  };
}

function buildTextDraft() {
  // 주소 + 상세주소는 "분리 값"으로 저장(복구 UX 좋게)
  return {
    title: v("title"),
    content: v("content"),

    room_type_id: v("roomTypeId"),
    monthly_rent: v("monthlyRent"),
    deposit: v("deposit"),
    area_m2: v("areaM2"),
    floor: v("floor"),

    address: v("address"),
    address_detail: v("addressDetail"),
    legal_dong: v("legalDong"),
    land_number: v("landNumber"),
    available_from: v("availableFrom"),
    max_roommates: v("maxRoommates"),
  };
}

function saveTextDraft() {
  saveDraft({
    key: TEXT_KEY,
    page: TEXT_PAGE,
    userScope: USER_SCOPE,
    version: VERSION,
    ttlMs: TTL_MS,
    data: buildTextDraft(),
  });
}

function loadTextDraft() {
  const d = loadDraft({
    key: TEXT_KEY,
    page: TEXT_PAGE,
    userScope: USER_SCOPE,
    version: VERSION,
  });
  if (!d || typeof d !== "object") return null;

  // 완전 빈 draft면 무시
  const meaningful = Object.keys(d).some((k) => String(d[k] ?? "").trim() !== "");
  return meaningful ? d : null;
}

function clearTextDraft() {
  clearDraft(TEXT_KEY);
}

function applyTextDraft(draft) {
  if (!draft) return;

  const setVal = (id, val) => {
    const el = document.getElementById(id);
    if (!el) return;
    el.value = (val ?? "");
  };

  setVal("title", draft.title);
  setVal("content", draft.content);

  setVal("roomTypeId", draft.room_type_id);
  setVal("monthlyRent", draft.monthly_rent);
  setVal("deposit", draft.deposit);
  setVal("areaM2", draft.area_m2);
  setVal("floor", draft.floor);

  setVal("address", draft.address);
  setVal("addressDetail", draft.address_detail);
  setVal("legalDong", draft.legal_dong);
  setVal("landNumber", draft.land_number);
  setVal("availableFrom", draft.available_from);
  setVal("maxRoommates", draft.max_roommates);
}

const saveTextDraftDebounced = debounce(saveTextDraft, 250);

function setupDraftAutoSave() {
  // 네 form DOM id 기준
  const ids = [
    "title",
    "content",
    "roomTypeId",
    "monthlyRent",
    "deposit",
    "areaM2",
    "floor",
    "address",
    "addressDetail",
    "legalDong",
    "landNumber",
    "availableFrom",
    "maxRoommates",
  ];

  ids.forEach((id) => {
    const el = document.getElementById(id);
    if (!el) return;
    el.addEventListener("input", saveTextDraftDebounced);
    el.addEventListener("change", saveTextDraftDebounced);
  });
}

// ===== room types =====
async function loadRoomTypes() {
  const select = document.getElementById("roomTypeId");
  if (!select) return;

  try {
    const res = await apiRequest("/api/room-types", { method: "GET" });
    if (!res.ok) {
      console.warn("[room-create] room types load fail:", res.status);
      return;
    }

    const list = await res.json();
    // 초기화(첫 option 유지)
    select.querySelectorAll("option:not(:first-child)").forEach((o) => o.remove());

    (Array.isArray(list) ? list : []).forEach((rt) => {
      const id = rt.roomTypeId ?? rt.room_type_id;
      const name = rt.roomTypeName ?? rt.room_type_name;
      if (id==null) return;

      const opt = document.createElement("option");
      opt.value = id;
      opt.textContent = name ?? `타입-${id}`;
      select.appendChild(opt);
    });
  } catch (e) {
    console.error("[room-create] loadRoomTypes error:", e);
  }
}

function setupAddressSearch() {
  const btn = document.getElementById("btnAddrSearch");
  if (!btn) return;

  btn.addEventListener("click", () => {
    new daum.Postcode({
      oncomplete: function (data) {
        // 도로명/지번 중 있으면 우선 적용
        const road = data.roadAddress || "";
        const jibun = data.jibunAddress || "";
        const addr = road || jibun;

        document.getElementById("address").value = addr;

        // 법정동(있을 때만)
        document.getElementById("legalDong").value = data.bname || "";

        // 지번은 데이터가 파편화되어 있어서 MVP면 비우고, 나중에 정교화 추천
        document.getElementById("landNumber").value = "";

        // 상세주소로 포커스 이동
        document.getElementById("addressDetail")?.focus();

        saveTextDraftDebounced();
      },
    }).open();
  });
}

// ===== temp upload =====
async function uploadTempRoomImage(file) {
  const formData = new FormData();
  formData.append("file", file);

  const res = await apiRequest("/api/files/temp/room", {
    method: "PUT",
    body: formData,
  });

  if (!res.ok) {
    throw new Error("temp room upload failed");
  }

  const data = await res.json();
  return {
    temp_file_id: data.temp_file_id ?? data.tempFileId,
    temp_url: data.temp_url ?? data.tempPath ?? data.temp_path,
  };
}

// ===== storage/preview =====
function persistSelectedFiles() {
  saveDraft({
    key: IMG_KEY,
    page: IMG_PAGE,
    userScope: USER_SCOPE,
    version: VERSION,
    ttlMs: TTL_MS,
    data: selectedFiles,
  });
}

function clearPersistedFiles() {
  clearDraft(IMG_KEY);
}

function renderPreview(grid) {
  grid.innerHTML = "";
  selectedFiles.forEach((it, idx) => {
    const div = document.createElement("div");
    div.className = "preview-item";
    div.innerHTML = `
      <img src="${it.temp_url}" alt="preview">
      <button type="button" class="preview-remove" data-idx="${idx}">삭제</button>
    `;
    grid.appendChild(div);
  });
  persistSelectedFiles();
}

function loadTempImages() {
  const arr = loadDraft({
    key: IMG_KEY,
    page: IMG_PAGE,
    userScope: USER_SCOPE,
    version: VERSION,
  });
  return Array.isArray(arr) && arr.length > 0 ? arr : null;
}

function restoreDraftsWithPrompt(grid) {
  const textDraft = loadTextDraft();
  const imgDraft = loadTempImages();

  if (!textDraft && !imgDraft) return;

  const ok = confirm("이전에 작성하던 내용/사진이 남아있습니다.\n불러오시겠습니까?");
  if (!ok) {
    clearTextDraft();
    clearPersistedFiles();
    selectedFiles = [];
    grid.innerHTML = "";
    return;
  }

  if (textDraft) applyTextDraft(textDraft);
  if (imgDraft) {
    selectedFiles = imgDraft;
    renderPreview(grid);
  }
}
// ===== image preview =====
function setupImagePreview() {
  const input = document.getElementById("photoInput");
  const grid = document.getElementById("previewGrid");
  if (!input || !grid) return;

  restoreDraftsWithPrompt(grid);

  input.addEventListener("change", async () => {
    const files = Array.from(input.files || []);
    if (files.length === 0) return;

    const remain = 10 - selectedFiles.length;
    if (remain <= 0) {
      alert("이미지는 최대 10장까지 업로드할 수 있습니다.");
      input.value = "";
      return;
    }

    const toUpload = files.slice(0, remain);
    if (files.length > remain) {
      alert(`이미지는 최대 10장까지 업로드할 수 있어요.\n${remain}장만 업로드합니다.`);
    }

    input.disabled = true;

    try {
      let failCount = 0;

      for (const file of toUpload) {
        if (!file.type.startsWith("image/")) {
          failCount++;
          continue;
        }
        try {
            const uploaded = await uploadTempRoomImage(file);

            if (!uploaded.temp_file_id || !uploaded.temp_url) {
              failCount++;
              continue;
            }

            selectedFiles.push(uploaded);
            renderPreview(grid);
        } catch (e) {
          console.error(e);
          failCount++;
        }
      }
      if (failCount > 0) {
        alert(`이미지 ${failCount}장 업로드에 실패했습니다.`);
      }
    } finally {
      input.value="";
      input.disabled = false;
    }
  });

  grid.addEventListener("click", (e) => {
    const btn = e.target.closest(".preview-remove");
    if (!btn) return;

    const idx = Number(btn.dataset.idx);
    if (Number.isNaN(idx)) return;

    selectedFiles.splice(idx, 1);
    renderPreview(grid);
  });
}

// ===== submit =====
async function submitRoom(e) {
  e.preventDefault();

  const form = document.getElementById("roomCreateForm");
  const submitBtn = form?.querySelector('button[type="submit"]');

  if (submitBtn?.dataset.loading === "true") return;
  if (submitBtn) {
    submitBtn.dataset.loading = "true";
    submitBtn.disabled = true;
  }

  // 주소 + 상세주소 합치기
  const addr = v("address");
  const addrDetail = v("addressDetail");
  const fullAddress = addrDetail ? `${addr} ${addrDetail}` : addr;

  const payload = {
    title: v("title"),
    content: v("content"),

    room_type_id: n("roomTypeId"),
    monthly_rent: n("monthlyRent"),
    deposit: n("deposit"),
    area_m2: n("areaM2"),
    floor: i("floor"),

    address: fullAddress,
    legal_dong: v("legalDong") || null,
    land_number: v("landNumber") || null,
    available_from: v("availableFrom") || null,
    max_roommates: i("maxRoommates"),

    temp_file_ids: selectedFiles.map((x) => x.temp_file_id),
  };

  const msg = validate(payload);
  if (msg) {
    alert(msg);
    if (submitBtn) {
      submitBtn.dataset.loading = "false";
      submitBtn.disabled = false;
    }
    return;
  }

  try {
    const res = await apiRequest("/api/rooms", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    if (!res.ok) {
      let m = "등록에 실패했습니다.";
      try {
        const err = await res.json();
        m = err?.message || m;
      } catch (_) {}
      alert(m);
      return;
    }

    const roomId = await res.json();
    clearPersistedFiles();
    clearTextDraft();
    alert("등록이 완료되었습니다.");
    window.location.href = "/rooms/" + roomId;
  } catch (e) {
    console.error("[room-create] submit error:", e);
    alert("서버 오류가 발생했습니다.");
  } finally {
     if (submitBtn) {
       submitBtn.dataset.loading = "false";
       submitBtn.disabled = false;
     }
  }
}

// ===== init =====
window.addEventListener("DOMContentLoaded", async () => {
  const ok = requireLogin();
  if (!ok) return;

  document.getElementById("btn-back")?.addEventListener("click", () => {
    if (history.length > 1) history.back();
    else window.location.href = "/rooms";
  });
  document.getElementById("btnCancel")?.addEventListener("click", () => {
    clearPersistedFiles();
    clearTextDraft();
    window.location.href = "/members/mypage";
  });

  setupImagePreview();
  setupAddressSearch();
  await loadRoomTypes();

  const textDraft = loadTextDraft();
  if (textDraft) {
    applyTextDraft(textDraft);
  }

  setupDraftAutoSave();
  document.getElementById("address")?.addEventListener("click", () => {
    document.getElementById("btnAddrSearch")?.click();
  });

  document.getElementById("roomCreateForm")?.addEventListener("submit", submitRoom);
});

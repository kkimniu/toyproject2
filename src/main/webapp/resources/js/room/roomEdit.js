// /resources/js/room/roomEdit.js
import { apiRequest } from "../common/apiClient.js";
import { requireLogin } from "../common/authGuard.js";
import { v, n, i } from "../common/formUtils.js";

document.addEventListener("DOMContentLoaded", async () => {
  const ok = requireLogin();
  if (!ok) return;

  const roomId = getRoomIdFromHidden();
  if (!roomId) {
    alert("잘못된 접근입니다.");
    location.href = "/members/mypage";
    return;
  }

  bindNavButtons();
  bindAddressSearch();

  const existingImageUrls = [];
  const newImageUrls = [];
  const tempFileIds = [];

  try {
    await loadRoomTypes();
    await loadRoomDetailAndFill(roomId, existingImageUrls);
    renderAllImages(existingImageUrls, newImageUrls, tempFileIds);
    bindImagePicker(existingImageUrls, newImageUrls, tempFileIds);
  } catch (e) {
    console.error(e);
    alert("수정 페이지 초기화에 실패했습니다.");
    location.href = "/members/mypage";
    return;
  }

  bindSubmit(roomId, existingImageUrls, newImageUrls, tempFileIds);
});

/* ------------------------------
 *  Basic Utils
 * ------------------------------ */

function getRoomIdFromHidden() {
  const raw = document.getElementById("roomId")?.value;
  const id = Number(raw);
  return Number.isFinite(id) ? id : null;
}

function setValue(id, val) {
  const el = document.getElementById(id);
  if (el) el.value = val ?? "";
}

function setSelect(id, val) {
  const el = document.getElementById(id);
  if (!el) return;
  el.value = String(val ?? "");
}

function toDateInputValue(raw) {
  return String(raw).slice(0, 10);
}

/* ------------------------------
 *  Navigation
 * ------------------------------ */

function bindNavButtons() {
  const back = document.getElementById("btn-back");
  const cancel = document.getElementById("btnCancel");

  const goMypage = () => (location.href = "/members/mypage");

  back?.addEventListener("click", goMypage);
  cancel?.addEventListener("click", goMypage);
}

/* ------------------------------
 *  Load Room Types
 * ------------------------------ */

async function loadRoomTypes() {
  const select = document.getElementById("roomTypeId");
  if (!select) return;

  const res = await apiRequest("/api/room-types", { method: "GET" });
  if (!res.ok) throw new Error("room types load failed: " + res.status);

  const list = await res.json();
  list.forEach((rt) => {
    const opt = document.createElement("option");
    opt.value = rt.roomTypeId ?? rt.room_type_id;
    opt.textContent = rt.roomTypeName ?? rt.room_type_name;
    select.appendChild(opt);
  });
}

/* ------------------------------
 *  Load Room Detail & Fill Form
 * ------------------------------ */

async function loadRoomDetailAndFill(roomId, existingImageUrls) {
  const res = await apiRequest(`/api/rooms/${roomId}`, { method: "GET" });

  if (res.status === 401) {
    alert("로그인이 필요합니다.");
    location.href = "/members/login";
    throw new Error("unauthorized");
  }
  if (res.status === 403) {
    alert("작성자만 수정할 수 있습니다.");
    location.href = "/members/mypage";
    throw new Error("forbidden");
  }
  if (!res.ok) {
    alert("방 정보를 불러올 수 없습니다.");
    location.href = "/members/mypage";
    throw new Error("load failed");
  }

  const data = await res.json();

  // 값 채우기
  setValue("title", data.title ?? data.roomTitle ?? "");
  setValue("content", data.content ?? data.roomContent ?? "");
  setValue("address", data.address ?? "");
  setValue("legalDong", data.legalDong ?? data.legal_dong ?? "");
  setValue("landNumber", data.landNumber ?? data.land_number ?? "");

  setValue("monthlyRent", data.monthlyRent ?? data.monthly_rent ?? 0);
  setValue("deposit", data.deposit ?? 0);
  setValue("areaM2", data.areaM2 ?? data.area_m2 ?? 0);
  setValue("floor", data.floor ?? "");
  setValue("maxRoommates", data.maxRoommates ?? data.max_roommates ?? "");

  setSelect("roomTypeId", data.roomTypeId ?? data.room_type_id ?? "");

  const availableFrom = data.availableFrom ?? data.available_from;
  if (availableFrom) setValue("availableFrom", toDateInputValue(availableFrom));

  const urls = data.imageUrls ?? data.image_urls ?? [];
  existingImageUrls.splice(0, existingImageUrls.length, ...(Array.isArray(urls) ? urls : []));
}

/* ------------------------------
 *  Address Search (Daum Postcode)
 * ------------------------------ */

function bindAddressSearch() {
  const btn = document.getElementById("btnAddrSearch");
  const address = document.getElementById("address");

  const openPostcode = () => {
    new daum.Postcode({
      oncomplete: function (data) {
        setValue("address", data.address || "");
        setValue("legalDong", data.bname || "");
        setValue("landNumber", data.jibunAddress || "");
      },
    }).open();
  };

  btn?.addEventListener("click", openPostcode);
  address?.addEventListener("click", openPostcode);
}

/* ------------------------------
 *  Image Rendering (Existing + New)
 * ------------------------------ */

function renderAllImages(existingImageUrls, newImageUrls, tempFileIds) {
  const grid = document.getElementById("previewGrid");
  if (!grid) return;

  grid.innerHTML = "";

  existingImageUrls.forEach((url) => {
    const item = createPreviewItem(url, () => {
      const index = existingImageUrls.indexOf(url);
      if (index !== -1) existingImageUrls.splice(index, 1);
      renderAllImages(existingImageUrls, newImageUrls, tempFileIds);
    });
    grid.appendChild(item);
  });

  newImageUrls.forEach((url) => {
    const item = createPreviewItem(url, () => {
      const index = newImageUrls.indexOf(url);
      if (index !== -1) {
        newImageUrls.splice(index, 1);
        tempFileIds.splice(index, 1); // tempFileIds는 newImageUrls랑 같은 인덱스
      }
      renderAllImages(existingImageUrls, newImageUrls, tempFileIds);
    });
    grid.appendChild(item);
  });
}

function createPreviewItem(url, onRemove) {
  const item = document.createElement("div");
  item.className = "preview-item";

  const img = document.createElement("img");
  img.src = url;
  img.alt = "preview";

  const btn = document.createElement("button");
  btn.type = "button";
  btn.className = "preview-remove";
  btn.textContent = "삭제";
  btn.addEventListener("click", onRemove);

  item.appendChild(img);
  item.appendChild(btn);
  return item;
}

/* ------------------------------
 *  Temp Upload (New Images)
 * ------------------------------ */

function bindImagePicker(existingImageUrls, newImageUrls, tempFileIds) {
  const input = document.getElementById("photoInput");
  if (!input) return;

  input.addEventListener("change", async (e) => {
    const files = Array.from(e.target.files || []);
    if (files.length === 0) return;

    try {
      for (const file of files) {
        const uploaded = await uploadTempRoomImage(file);

        const tempId = uploaded?.temp_file_id;
        const url = uploaded?.temp_url;

        const hasTempId = tempId !== null && tempId !== undefined && String(tempId).trim() !== "";
        const hasUrl = typeof url === "string" && url.trim() !== "";

        if (hasTempId && hasUrl) {
          tempFileIds.push(Number(tempId));
          newImageUrls.push(url);
        } else {
          throw new Error("temp upload response invalid");
        }
      }

      renderAllImages(existingImageUrls, newImageUrls, tempFileIds);
    } catch (err) {
      console.error(err);
      alert("이미지 업로드에 실패했습니다.");
    } finally {
      input.value = "";
    }
  });
}

async function uploadTempRoomImage(file) {
  const formData = new FormData();
  formData.append("file", file);

  const res = await apiRequest("/api/files/temp/room", {
    method: "PUT",
    body: formData,
  });

  if (!res.ok) throw new Error("temp upload fail: " + res.status);

  const data = await res.json();
  return {
    temp_file_id: data.temp_file_id ?? data.tempFileId,
    temp_url: data.temp_url ?? data.tempUrl ?? data.temp_path ?? data.tempPath,
  };
}

/* ------------------------------
 *  Submit (PUT /api/rooms/{roomId})
 * ------------------------------ */

function bindSubmit(roomId, existingImageUrls, newImageUrls, tempFileIds) {
  const form = document.getElementById("roomEditForm");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const fullAddress = buildFullAddress();

    const keepImageUrls = existingImageUrls.filter((x) => typeof x === "string" && x.trim() !== "");
    const validTempFileIds = tempFileIds.filter((x) => Number.isFinite(Number(x)));

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

      image_urls: keepImageUrls,
      temp_file_ids: validTempFileIds,
    };

    // 최소 검증
    if (!payload.title || !payload.content || !payload.address || !payload.room_type_id) {
      alert("필수 항목(제목/설명/주소/방타입)을 입력해 주세요.");
      return;
    }

    const res = await apiRequest(`/api/rooms/${roomId}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    if (res.status === 401) {
      alert("로그인이 필요합니다.");
      location.href = "/members/login";
      return;
    }
    if (res.status === 403) {
      alert("작성자만 수정할 수 있습니다.");
      location.href = "/members/mypage";
      return;
    }
    if (!res.ok) {
      alert("수정에 실패했습니다.");
      return;
    }

    alert("수정되었습니다.");
    location.replace(`/rooms/${roomId}`);
  });
}

function buildFullAddress() {
  const base = v("address");
  const detail = v("addressDetail");
  if (!detail) return base;
  return `${base} ${detail}`.trim();
}

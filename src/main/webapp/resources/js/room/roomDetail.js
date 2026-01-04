// /resources/js/room/roomDetail.js
import { apiRequest } from "../common/apiClient.js";
import { requireLogin } from "../common/authGuard.js";

let currentMemberId = null;
let currentRoom = null;

//  이벤트 중복 바인딩 방지용
let actionsBound = false;

// ====== utils ======

function normalizeRoomId(roomIdRaw) {
  if (roomIdRaw == null) return null;
  const s = String(roomIdRaw).trim();
  if (!s || s === "undefined" || s === "null") return null;
  if (!/^\d+$/.test(s)) return null; // (유지) 나중에 UUID면 여기만 완화
  return s;
}

function isOwnerOfRoom(room) {
  const ownerId = room?.ownerId ?? room?.owner_id ?? null;
  return ownerId && currentMemberId && String(ownerId) === String(currentMemberId);
}

function formatNumber(num) {
  if (num == null) return "-";
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function formatDate(dateStr) {
  if (!dateStr) return "";
  const d = new Date(dateStr);
  if (isNaN(d.getTime())) return "";
  return d.toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
}

function getRoomMeta() {
  const metaEl = document.getElementById("room-detail-data");
  if (!metaEl) return { roomId: null };
  const roomId = normalizeRoomId(metaEl.dataset.roomId);
  return { roomId };
}

async function fetchCurrentMemberId() {
  try {
    const res = await apiRequest("/api/members/me", { method: "GET" });
    if (!res.ok) return null;
    const data = await res.json();
    return data.memberId ?? data.member_id ?? null;
  } catch (e) {
    console.error("[room-detail] fetchCurrentMemberId error:", e);
    return null;
  }
}

function applyFavoriteButtonState(btn, favorited) {
  if (!btn) return;
  if (favorited) {
    btn.classList.add("active");
    btn.textContent = "찜 완료";
  } else {
    btn.classList.remove("active");
    btn.textContent = "찜하기";
  }
}

// ====== chips (더보기/접기) ======
function renderChips(containerEl, tags, options = {}) {
  const {
    max = 8,
    emptyText = "선호 태그 없음",
    moreText = "더보기",
    lessText = "접기",
  } = options;

  if (!containerEl) return;

  const safeTags = Array.isArray(tags) ? tags.filter(Boolean) : [];

  if (safeTags.length === 0) {
    //  이전 expanded 상태 찌꺼기 제거
    delete containerEl.dataset.expanded;

    containerEl.innerHTML = "";
    const span = document.createElement("span");
    span.className = "chip room-chip";
    span.innerText = emptyText;
    containerEl.appendChild(span);
    return;
  }

  if (max == null) {
    delete containerEl.dataset.expanded;

    containerEl.innerHTML = "";
    safeTags.forEach((text) => {
      const span = document.createElement("span");
      span.className = "chip room-chip";
      span.innerText = text;
      containerEl.appendChild(span);
    });
    return;
  }

  //  렌더 재호출돼도 상태 유지
  let expanded = containerEl.dataset.expanded === "true";

  function draw() {
    containerEl.innerHTML = "";

    const visible = expanded ? safeTags : safeTags.slice(0, max);

    visible.forEach((text) => {
      const span = document.createElement("span");
      span.className = "chip room-chip";
      span.innerText = text;
      containerEl.appendChild(span);
    });

    if (safeTags.length > max) {
      const btn = document.createElement("button");
      btn.type = "button";
      btn.className = "chip room-chip chip-more-btn";
      btn.innerText = expanded ? lessText : moreText;

      btn.addEventListener("click", (e) => {
        e.preventDefault();
        e.stopPropagation();
        expanded = !expanded;
        containerEl.dataset.expanded = String(expanded);
        draw();
      });

      containerEl.appendChild(btn);
    }
  }

  draw();
}

//  액션 영역(guest/owner/not-owner) + 찜/문의 버튼까지 한 번에 정리
function applyActionVisibility(room) {
  const guestEl = document.getElementById("action-guest");
  const notOwnerEl = document.getElementById("action-not-owner");
  const ownerEl = document.getElementById("action-owner");

  const likeBtn = document.getElementById("btn-like");
  const chatBtn = document.getElementById("btn-start-chat");

  if (guestEl) guestEl.style.display = "none";
  if (notOwnerEl) notOwnerEl.style.display = "none";
  if (ownerEl) ownerEl.style.display = "none";

  // 비로그인
  if (!currentMemberId) {
    if (guestEl) guestEl.style.display = "block";

    // 비로그인 UX: 찜/문의 버튼 숨김
    if (likeBtn) likeBtn.style.display = "none";
    if (chatBtn) chatBtn.style.display = "none";
    return;
  }
  // 로그인
  const isOwner = isOwnerOfRoom(room);
  if (isOwner) {
    if (ownerEl) ownerEl.style.display = "flex";
    if (likeBtn) likeBtn.style.display = "none";
    if (chatBtn) chatBtn.style.display = "none";
  } else {
    if (notOwnerEl) notOwnerEl.style.display = "flex";
    if (likeBtn) likeBtn.style.display = "";
    if (chatBtn) chatBtn.style.display = "";
  }
}

// ===================== init =====================

window.addEventListener("DOMContentLoaded", async () => {
  const { roomId } = getRoomMeta();

  if (!roomId) {
    console.warn("[room-detail] roomId가 유효하지 않습니다.");
    return;
  }

  const backBtn = document.getElementById("btn-back");
  if (backBtn) {
    backBtn.addEventListener("click", () => {
      location.href = "/rooms/map";
    });
  }

  const [room, fetchedMemberId] = await Promise.all([
    loadRoomDetail(roomId),
    fetchCurrentMemberId(),
  ]);

  currentRoom = room;
  currentMemberId = fetchedMemberId;

  if (!currentRoom) return;

  renderRoomDetail(currentRoom);
  applyActionVisibility(currentRoom);

  bindActionsOnce(roomId);
  bindDetailActions();
});

function bindActionsOnce(roomId) {
  if (actionsBound) return;
  actionsBound = true;

  // 찜하기 버튼
  const likeBtn = document.getElementById("btn-like");
  if (likeBtn && likeBtn.style.display !== "none") {
    likeBtn.addEventListener("click", async () => {
      const ok = requireLogin();
      if (!ok) return;

      if (likeBtn.dataset.loading === "true") return;
      likeBtn.dataset.loading = "true";
      likeBtn.disabled = true;

      try {
          const currentlyFavorited = likeBtn.classList.contains("active");
          const nextFavorited = !currentlyFavorited;

        if (nextFavorited) {
          const res = await apiRequest(`/api/favorites/${roomId}`, { method: "POST" });
          if (!res.ok) throw new Error("favorite failed");
        } else {
          const res = await apiRequest(`/api/favorites/${roomId}`, { method: "DELETE" });
          if (!res.ok && res.status !== 404) throw new Error("unfavorite failed");
        }

        //  room 캐시 동기화 (favorited / isFavorited 둘 다)
        if (currentRoom) {
          currentRoom.favorited = nextFavorited;
          currentRoom.isFavorited = nextFavorited;
        }

        applyFavoriteButtonState(likeBtn, nextFavorited);
      } catch (e) {
        console.error("[room-detail] favorite toggle error:", e);
        alert("관심 설정 중 오류가 발생했습니다.");
      } finally {
        likeBtn.dataset.loading = "false";
        likeBtn.disabled = false;
      }
    });
  }

  // 문의하기 버튼
  const chatBtn = document.getElementById("btn-start-chat");
  if (chatBtn) {
    chatBtn.addEventListener("click", () => {
      const ok = requireLogin();
      if (!ok) return;
      alert(`roomId=${roomId} 방 작성자와 채팅을 시작하는 기능은 추후 연동 예정입니다.`);
    });
  }

  // 프로필 보기
  const viewProfileBtn = document.getElementById("btn-view-profile");
  if (viewProfileBtn) {
    viewProfileBtn.addEventListener("click", () => {
      const ownerId = currentRoom?.ownerId ?? currentRoom?.owner_id ?? null;
      if (!ownerId) return;
      if (isOwnerOfRoom(currentRoom)) {
        location.href = "/members/mypage";
        return;
      }
      location.href = `/members/${ownerId}`;
    });
  }
  // 게시글 수정 (owner 전용)
  const editBtn = document.getElementById("btn-detail-edit");
  if (editBtn) {
    editBtn.addEventListener("click", (e) => {
      e.preventDefault();
      e.stopPropagation();

      const ok = requireLogin();
      if (!ok) return;

      // owner만 보이게 되어있지만, 혹시 몰라 한번 더 방어
      if (!isOwnerOfRoom(currentRoom)) {
        alert("작성자만 수정할 수 있습니다.");
        return;
      }

      location.href = `/rooms/${roomId}/edit`;
    });
  }
  // 게시글 삭제 (owner 전용)
  const deleteBtn = document.getElementById("btn-detail-delete");
  if (deleteBtn) {
    deleteBtn.addEventListener("click", async (e) => {
      e.preventDefault();
      e.stopPropagation();

      const ok = requireLogin();
      if (!ok) return;

      if (!isOwnerOfRoom(currentRoom)) {
        alert("작성자만 삭제할 수 있습니다.");
        return;
      }

      const confirmed = confirm("게시글을 삭제하면 더 이상 노출되지 않습니다.\n정말 삭제하시겠습니까?");
      if (!confirmed) return;

      if (deleteBtn.dataset.loading === "true") return;
      deleteBtn.dataset.loading = "true";
      deleteBtn.disabled = true;

      try {
        const res = await apiRequest(`/api/rooms/${roomId}`, { method: "DELETE" });

        if (res.status === 401) {
          alert("로그인이 필요합니다.");
          location.href = "/members/login";
          return;
        }
        if (res.status === 403) {
          alert("작성자만 삭제할 수 있습니다.");
          location.href = "/members/mypage";
          return;
        }
        if (!res.ok) {
          alert("삭제에 실패했습니다.");
          return;
        }

        alert("삭제되었습니다.");
        // 삭제 후 이동 정책은 팀 기준에 맞춰 선택
        location.href = "/members/mypage"; // 또는 "/rooms/map"
      } catch (err) {
        console.error(err);
        alert("삭제 중 오류가 발생했습니다.");
      } finally {
        deleteBtn.dataset.loading = "false";
        deleteBtn.disabled = false;
      }
    });
  }
}

// ===================== API =====================

async function loadRoomDetail(roomId) {
  try {
    const res = await apiRequest(`/api/rooms/${roomId}`, { method: "GET" });
    if (!res.ok) {
      console.error("[room-detail] room detail load fail:", res.status);
      return null;
    }
    return await res.json();
  } catch (e) {
    console.error("[room-detail] room detail error:", e);
    return null;
  }
}

// ===================== Render =====================

function renderRoomDetail(room) {
  const titleEl = document.querySelector(".room-detail-title");
  if (titleEl) {
    titleEl.textContent = room.roomTitle ?? room.room_title ?? room.title ?? "방 제목";
  }
  const imageUrls = room.imageUrls ?? room.image_urls ?? [];
  const validUrls = imageUrls.filter(Boolean);
  const mainImgEl = document.getElementById("room-main-image");
  if (mainImgEl) {
    const first = imageUrls.length > 0 ? imageUrls[0] : null;
    mainImgEl.src = first ? first : "/resources/img/room/default-room.jpg"; //  빈 문자열 방어
  }

  const thumbsWrap = document.getElementById("room-thumbnails");
  if (thumbsWrap) {
    thumbsWrap.innerHTML = "";
    if (validUrls.length > 1) {
      thumbsWrap.style.display = "flex";
      validUrls.forEach((url, idx) => {
        const img = document.createElement("img");
        img.src = url;
        img.alt = `방 이미지 ${idx + 1}`;
        img.className = "room-gallery-thumb" + (idx === 0 ? " active" : "");
        img.addEventListener("click", () => {
          if (mainImgEl) mainImgEl.src = url;
          [...thumbsWrap.querySelectorAll("img")].forEach((el) =>
            el.classList.remove("active")
          );
          img.classList.add("active");
        });
        thumbsWrap.appendChild(img);
      });
    } else {
      thumbsWrap.style.display = "none";
    }
  }

  const createdAtEl = document.getElementById("room-created-at");
  if (createdAtEl) {
    createdAtEl.innerText = formatDate(room.roomCreatedAt ?? room.room_created_at);
  }

  const monthlyRent = room.monthlyRent ?? room.monthly_rent;
  const deposit = room.deposit;

  const monthlyManwon = monthlyRent != null ? Math.round(monthlyRent / 10000) : null;
  const monthlyEl = document.getElementById("room-monthly");
  if (monthlyEl) {
    monthlyEl.innerText =
      monthlyManwon != null ? `월 ${formatNumber(monthlyManwon)}만원` : "월세 정보 없음";
  }

  const depositManwon = deposit != null ? Math.round(deposit / 10000) : null;
  const depositEl = document.getElementById("room-deposit");
  if (depositEl) {
    depositEl.innerText =
      depositManwon != null ? `보증금 ${formatNumber(depositManwon)}만원` : "보증금 정보 없음";
  }

  const roomTypeName = room.roomTypeName ?? room.room_type_name;
  const roomCountEl = document.getElementById("room-summary-room-count");
  if (roomCountEl) roomCountEl.innerText = roomTypeName ? roomTypeName : "-";

  const area = room.areaM2 ?? room.area_m2;
  const areaEl = document.getElementById("room-summary-area-m2");
  if (areaEl) areaEl.innerText = area != null ? `${area}㎡` : "-";

  const floorEl = document.getElementById("room-summary-room-floor");
  if (floorEl) floorEl.innerText = room.floor != null ? `${room.floor}층` : "-";

  const max = room.maxRoommates ?? room.max_roommates;
  const maxEl = document.getElementById("room-summary-room-max");
  if (maxEl) maxEl.innerText = max != null ? `${max}명` : "-";

  const available = room.availableFrom ?? room.available_from;
  const availableEl = document.getElementById("room-available-from");
  if (availableEl) availableEl.innerText = available ? formatDate(available) : "협의 가능";

  const statusPill = document.getElementById("room-status-pill");
  if (statusPill) {
    const status = room.status;
    let text = "비공개";
    if (status === "OPEN") text = "모집중";
    else if (status === "RESERVED") text = "예약중";
    else if (status === "CLOSED") text = "마감";
    statusPill.innerText = text;
    statusPill.className = "room-status-pill";
    if (status) statusPill.classList.add(`room-status-${status}`);
  }

  const contentEl = document.getElementById("room-content");
  if (contentEl) contentEl.innerText = room.content || "";

  const views = room.views ?? 0;
  const authorViewsEl = document.getElementById("author-views");
  if (authorViewsEl) authorViewsEl.innerText = views;

  const authorName = room.ownerName ?? room.owner_name ?? "작성자";
  const authorPhoto = room.ownerPhotoUrl ?? room.owner_photo_url ?? "";
  const authorPhotoEl = document.getElementById("author-photo");
  if (authorPhotoEl) authorPhotoEl.src = authorPhoto || "/resources/img/default-user.png";

  const authorNameEl = document.getElementById("author-name");
  if (authorNameEl) authorNameEl.innerText = authorName;

  const authorJoinedEl = document.getElementById("author-joined");
  if (authorJoinedEl) {
    authorJoinedEl.innerText = formatDate(room.ownerJoinedAt ?? room.owner_joined_at);
  }

  //  찜 상태는 렌더에서 확정(초기/재렌더 모두 안정)
  const likeBtn = document.getElementById("btn-like");
  if (likeBtn) {
    const favorited = !!(room.favorited ?? room.isFavorited);
    applyFavoriteButtonState(likeBtn, favorited);
  }

  const chipContainer = document.getElementById("room-preference-chips");
  const ownerTags = room.ownerTags ?? room.owner_tags ?? [];
  renderChips(chipContainer, ownerTags, { max: 8, emptyText: "선호 태그 없음" });
  applyActionVisibility(room);
}

function bindDetailActions() {
  const toggleBtn = document.getElementById("btn-detail-toggle-status");
  if (!toggleBtn) return;

  toggleBtn.addEventListener("click", async (e) => {
    e.preventDefault();
    e.stopPropagation();

    const ok = requireLogin();
    if (!ok) return;

    const { roomId } = getRoomMeta();
    if (!roomId) {
      alert("roomId를 찾을 수 없습니다.");
      return;
    }

    if (!isOwnerOfRoom(currentRoom)) {
      alert("작성자만 마감/재개할 수 있습니다.");
      return;
    }

    const pill = document.getElementById("room-status-pill");
    if (!pill) {
      alert("상태 표시 요소를 찾지 못했습니다.");
      return;
    }

    const currentStatus =
      pill.classList.contains("room-status-OPEN") ? "OPEN" :
      pill.classList.contains("room-status-RESERVED") ? "RESERVED" :
      pill.classList.contains("room-status-CLOSED") ? "CLOSED" :
      pill.classList.contains("room-status-HIDDEN") ? "HIDDEN" :
      "OPEN"; // 기본값

    if (currentStatus === "RESERVED" || currentStatus === "HIDDEN") {
      alert("현재 상태에서는 변경할 수 없습니다.");
      return;
    }

    const nextStatus = currentStatus === "OPEN" ? "CLOSED" : "OPEN";
    const confirmMsg = nextStatus === "CLOSED" ? "모집을 마감할까요?" : "모집을 재개할까요?";
    if (!confirm(confirmMsg)) return;

    if (toggleBtn.dataset.loading === "true") return;
    toggleBtn.dataset.loading = "true";
    toggleBtn.disabled = true;

    try {
      const res = await apiRequest(`/api/rooms/${roomId}/status`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ status: nextStatus }),
      });

      if (res.status === 401) {
        alert("로그인이 필요합니다.");
        location.href = "/members/login";
        return;
      }
      if (res.status === 403) {
        alert("작성자만 변경할 수 있습니다.");
        return;
      }
      if (!res.ok) {
        const msg = await res.text().catch(() => "");
        alert("상태 변경 실패" + (msg ? `: ${msg}` : ""));
        return;
      }

      pill.classList.remove(
        "room-status-OPEN",
        "room-status-RESERVED",
        "room-status-CLOSED",
        "room-status-HIDDEN"
      );
      pill.classList.add(`room-status-${nextStatus}`);
      pill.textContent = statusText(nextStatus);

      toggleBtn.textContent = nextStatus === "OPEN" ? "모집 마감" : "모집 재개";

      if (currentRoom) currentRoom.status = nextStatus;
    } catch (err) {
      console.error(err);
      alert("상태 변경 중 오류가 발생했습니다.");
    } finally {
      toggleBtn.dataset.loading = "false";
      toggleBtn.disabled = false;
    }
  });
}

function statusText(status) {
  switch (status) {
    case "OPEN": return "모집중";
    case "RESERVED": return "예약중";
    case "CLOSED": return "마감";
    case "HIDDEN": return "비공개";
    default: return status;
  }
}
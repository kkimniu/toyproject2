// /resources/js/room/roomDetail.js
import { apiRequest } from "../common/apiClient.js";
import { requireLogin } from "../common/authGuard.js";

// 숫자 3자리 콤마
function formatNumber(num) {
  if (num == null) return "-";
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// 날짜 포맷(yyyy년 M월 d일)
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

function toggleActionButtons({ currentMemberId, ownerId }) {
  const guestEl = document.getElementById("action-guest");
  const notOwnerEl = document.getElementById("action-not-owner");
  const ownerEl = document.getElementById("action-owner");

  // 초기 숨김
  if (guestEl) guestEl.style.display = "none";
  if (notOwnerEl) notOwnerEl.style.display = "none";
  if (ownerEl) ownerEl.style.display = "none";

  if (!currentMemberId) {
    // 비로그인
    if (guestEl) guestEl.style.display = "block";
    return;
  }

  // 로그인 상태
  if (ownerId && String(ownerId) === String(currentMemberId)) {
    // 내가 올린 방
    if (ownerEl) ownerEl.style.display = "flex";
  } else {
    // 다른 사람이 올린 방
    if (notOwnerEl) notOwnerEl.style.display = "flex";
  }
}

// JSP에서 내려준 roomId / ownerId 읽기
function getRoomMeta() {
  const metaEl = document.getElementById("room-detail-data");
  if (!metaEl) return { roomId: null };

  const roomId = metaEl.dataset.roomId || null;

  return { roomId };
 }

// 현재 로그인한 사용자 ID 조회 (/api/members/me)
async function fetchCurrentMemberId() {
  try {
    const res = await apiRequest("/api/members/me", { method: "GET" });

    if (!res.ok) {
      // 401 등 → 비로그인으로 취급
      console.debug("[room-detail] not logged in (status=", res.status, ")");
      return null;
    }

    const data = await res.json();
    const memberId = data.memberId ?? data.member_id ?? null;
    console.debug("[room-detail] currentMemberId =", memberId);
    return memberId;
  } catch (e) {
    console.error("[room-detail] fetchCurrentMemberId error:", e);
    return null;
  }
}


// 찜 버튼 상태 적용 유틸
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

window.addEventListener("DOMContentLoaded", async () => {
  const {  roomId } = getRoomMeta();

  if (!roomId) {
    console.warn("[room-detail] roomId를 찾을 수 없습니다.");
    return;
  }

  // 뒤로가기 버튼
  const backBtn = document.getElementById("btn-back");
  if (backBtn) {
    backBtn.addEventListener("click", () => {
      if (history.length > 1) {
        history.back();
      } else {
        location.href = "/rooms/map";
      }
    });
  }

 // 방 상세 + 현재 로그인 유저 정보 병렬 조회
  const [room, currentMemberId] = await Promise.all([
    loadRoomDetail(roomId),
    fetchCurrentMemberId(),
  ]);

  if (!room) return;

  // 작성자/로그인 여부에 따라 액션 버튼 노출
  const ownerId = room.ownerId ?? room.owner_id ?? null;
  toggleActionButtons({ currentMemberId, ownerId });

  // ♡ 찜하기 버튼
  const likeBtn = document.getElementById("btn-like");
  if (likeBtn) {
    // 1) 최초 상태 세팅: JSP에서 data-favorited="true|false" 내려줬다고 가정
    const initialFavorited = !!(room.favorited ?? room.isFavorited);
    applyFavoriteButtonState(likeBtn, initialFavorited);

    // 2) 클릭 시 토글 + API 호출
    likeBtn.addEventListener("click", async () => {
      const ok = requireLogin();
      if (!ok) return;

      const currentlyFavorited = likeBtn.classList.contains("active");
      const nextFavorited = !currentlyFavorited;

      try {
        if (nextFavorited) {
          //  찜 추가
          const res = await apiRequest(`/api/favorites/${roomId}`, {
            method: "POST",
          });
          if (!res.ok) {
            throw new Error("favorite failed");
          }
        } else {
          //  찜 해제
          const res = await apiRequest(`/api/favorites/${roomId}`, {
            method: "DELETE",
          });
          // 404(이미 없음)는 그냥 무시해도 됨
          if (!res.ok && res.status !== 404) {
            throw new Error("unfavorite failed");
          }
        }

        applyFavoriteButtonState(likeBtn, nextFavorited);
      } catch (e) {
        console.error("[room-detail] favorite toggle error:", e);
        alert("관심 설정 중 오류가 발생했습니다.");
      }
    });
  }

  // 💬 문의하기 버튼
  const chatBtn = document.getElementById("btn-start-chat");
  if (chatBtn) {
    chatBtn.addEventListener("click", () => {
        const ok = requireLogin();
        if (!ok) return;
            // TODO: 채팅방 생성 API 연동
            // const res = await apiRequest("/api/chat/rooms", {
            //   method: "POST",
            //   body: JSON.stringify({ roomId }),
            // });
        alert(
          `roomId=${roomId} 방 작성자와 채팅을 시작하는 기능은 추후 WebSocket/채팅 API 연동 예정입니다.`
        );
    });
  }

  // 👤 프로필 보기 버튼 (있으면)
  const viewProfileBtn = document.getElementById("btn-view-profile");
  if (viewProfileBtn && ownerId) {
    viewProfileBtn.addEventListener("click", () => {
      // TODO: 실제 프로필 페이지 라우팅 생기면 여기 수정
      // location.href = `/members/${ownerId}`;
      alert(`작성자 프로필 페이지로 이동 예정 (memberId=${ownerId})`);
    });
  }
});

// ===================== API 호출 + 렌더링 =====================

async function loadRoomDetail(roomId) {
  try {
    const res = await apiRequest(`/api/rooms/${roomId}`, { method: "GET" });
    if (!res.ok) {
      console.error("room detail load fail:", res.status);
      return;
    }

    const room = await res.json();
    console.log("[room-detail] room =", room);

    renderRoomDetail(room);
    return room;
  } catch (e) {
    console.error("room detail error:", e);
    return null;
  }
}

function renderRoomDetail(room) {
  // --- 이미지 (대표 이미지 + 썸네일) ---
  const imageUrls = room.imageUrls ?? room.image_urls ?? [];
  const mainImgEl = document.getElementById("room-main-image");
  if (mainImgEl) {
    if (imageUrls.length > 0) {
      mainImgEl.src = imageUrls[0];
    } else {
      mainImgEl.src = "/resources/img/room/default-room.jpg";
    }
  }

  const thumbsWrap = document.getElementById("room-thumbnails");
  if (thumbsWrap) {
    thumbsWrap.innerHTML = "";
    if (imageUrls.length > 1) {
      thumbsWrap.style.display = "flex";
      imageUrls.forEach((url, idx) => {
        const img = document.createElement("img");
        img.src = url;
        img.alt = `방 이미지 ${idx + 1}`;
        img.className = "room-gallery-thumb" + (idx === 0 ? " active" : "");
        img.addEventListener("click", () => {
          if (mainImgEl) mainImgEl.src = url;
          // active 토글
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

  // --- 상단 메타: 작성일 ---
  const createdAtEl = document.getElementById("room-created-at");
  if (createdAtEl) {
    createdAtEl.innerText = formatDate(
      room.roomCreatedAt ?? room.room_created_at
    );
  }

  // --- 월세/보증금 ---
  const monthlyRent = room.monthlyRent ?? room.monthly_rent;
  const deposit = room.deposit;

  const manwon = monthlyRent != null ? Math.round(monthlyRent / 10000) : null;

  const monthlyEl = document.getElementById("room-monthly");
  if (monthlyEl) {
    monthlyEl.innerText =
      manwon != null ? `월 ${formatNumber(manwon)}만원` : "월세 정보 없음";
  }

  const depositEl = document.getElementById("room-deposit");
  if (depositEl) {
    depositEl.innerText =
      deposit != null ? `보증금 ${formatNumber(deposit)}원` : "보증금 정보 없음";
  }

  // --- 요약 정보 카드 ---
  const roomCountEl = document.getElementById("room-summary-room-count");
  if (roomCountEl) {
    // roomTypeName이 있으면 그걸 우선 사용
    if (room.roomTypeName) {
      roomCountEl.innerText = room.roomTypeName;
    } else if (room.roomCount != null) {
      roomCountEl.innerText = `${room.roomCount}룸`;
    } else {
      roomCountEl.innerText = "-";
    }
  }

  const areaEl = document.getElementById("room-summary-area-m2");
  const area = room.areaM2 ?? room.area_m2;
  if (areaEl) {
    areaEl.innerText = area != null ? `${area}㎡` : "-";
  }

  const floorEl = document.getElementById("room-summary-room-floor");
  if (floorEl) {
    const floor = room.floor;
    floorEl.innerText = floor != null ? `${floor}층` : "-";
  }

  const maxEl = document.getElementById("room-summary-room-max");
  if (maxEl) {
    const max = room.maxRoommates ?? room.max_roommates;
    maxEl.innerText = max != null ? `${max}명` : "-";
  }

  // 입주 가능일
  const availableEl = document.getElementById("room-available-from");
  if (availableEl) {
    const available = room.availableFrom ?? room.available_from;
    availableEl.innerText = available ? formatDate(available) : "협의 가능";
  }

  // 상태 배지
  const statusPill = document.getElementById("room-status-pill");
  if (statusPill) {
    const status = room.status;
    let text = "비공개";
    if (status === "OPEN") text = "모집중";
    else if (status === "RESERVED") text = "예약중";
    else if (status === "CLOSED") text = "마감";

    statusPill.innerText = text;

    // className 재설정 (기존 class 유지 + 상태 class)
    statusPill.className = "room-status-pill";
    if (status) {
      statusPill.classList.add(`room-status-${status}`);
    }
  }

  // --- 상세 내용 ---
  const contentEl = document.getElementById("room-content");
  if (contentEl) {
    contentEl.innerText = room.content || "";
  }

  // --- 조회/관심수 (오른쪽 카드) ---
  const views = room.views ?? 0;

  const authorViewsEl = document.getElementById("author-views");
  if (authorViewsEl) authorViewsEl.innerText = views;

  // --- 작성자 정보 ---
  const authorName = room.ownerNickname ?? room.owner_nickname ?? "작성자";
  const authorPhoto = room.ownerPhotoUrl ?? room.owner_photo_url ?? "";

  const authorPhotoEl = document.getElementById("author-photo");
  if (authorPhotoEl) {
    authorPhotoEl.src =
      authorPhoto || "/resources/img/default-user.png";
  }

  const authorNameEl = document.getElementById("author-name");
  if (authorNameEl) authorNameEl.innerText = authorName;

  const authorJoinedEl = document.getElementById("author-joined");
  if (authorJoinedEl) {
    authorJoinedEl.innerText = formatDate(
      room.ownerJoinedAt ?? room.owner_joined_at
    );
  }

  // --- 선호 조건(지금은 mock) ---
  const prefs = ["깔끔함", "조용함", "직장인", "금연", "금주"]; // TODO: room.preferenceList 로 교체
  const chipWrap = document.getElementById("room-preference-chips");
  if (chipWrap) {
    chipWrap.innerHTML = "";
    prefs.forEach((p) => {
      const span = document.createElement("span");
      span.className = "room-chip";
      span.innerText = p;
      chipWrap.appendChild(span);
    });
  }
}
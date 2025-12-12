// ES Module
// /resources/js/room/roomMap.js
import { apiRequest } from "../common/apiClient.js";
import { requireLogin } from "../common/authGuard.js";

let map;
let clusterer;
let selectedRoomId = null;
let markers = [];
let currentRooms = []; // /api/rooms/map 에서 받아온 방 목록
let currentMemberId = null;

function normalizeRoomId(roomIdRaw) {
  if (roomIdRaw == null) return null;
  const s = String(roomIdRaw).trim();
  if (!s || s === "undefined" || s === "null") return null;
  if (!/^\d+$/.test(s)) return null;
  return s;
}

function formatNumber(num) {
  if (num === null || num === undefined) return "-";
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function cutText(text, maxLen) {
  if (!text) return "";
  if (text.length <= maxLen) return text;
  return text.substring(0, maxLen) + "…";
}

window.addEventListener("load", async () => {
  const container = document.getElementById("map");
  const options = {
    center: new kakao.maps.LatLng(37.5665, 126.9780), // 서울
    level: 6,
  };

  map = new kakao.maps.Map(container, options);

  clusterer = new kakao.maps.MarkerClusterer({
    map: map,
    averageCenter: true,
    minLevel: 7,
  });

  currentMemberId = await fetchCurrentMemberId();
  // 초기 1회
  fetchRoomsForCurrentBounds();
  let idleTimer = null;
  // 지도 이동/줌 이후
  kakao.maps.event.addListener(map, "idle", () => {
    clearTimeout(idleTimer);
    idleTimer = setTimeout(() => {
      fetchRoomsForCurrentBounds();
    }, 300);
  });
  // 오른쪽 카드 X 버튼
  document.getElementById("room-close-btn").addEventListener("click", () => {
    selectedRoomId = null;
    document.getElementById("room-detail-body").style.display = "none";
    document.getElementById("room-detail-footer").style.display = "none";
    document.getElementById("room-image").style.display = "none";
  // 1) 카드만 접기 (위/아래 카드만 보이게)
  //document.getElementById("room-detail-card").style.display = "none";

  // 2) 카드 틀은 남기고 "마커를 클릭해 방 정보를..." 이런 문구 넣고 싶으면:
   document.getElementById("room-detail-empty").style.display = "block";
  });

  // 상세 보기 버튼 → 뷰 페이지로 이동
  document.getElementById("btn-detail").addEventListener("click", () => {
  if (!selectedRoomId) return;

  // 로그인 안 되어 있으면 모달 띄우고 이동 막기
  if (!requireLogin()) {
    return;
  }
  const contextPath = window.contextPath || "";
  window.location.href = `${contextPath}/rooms/${selectedRoomId}`;
});

    const btnFavorite = document.getElementById("btn-favorite");
    if (btnFavorite) {
      btnFavorite.addEventListener("click", async () => {
        if (!selectedRoomId) return;

        // 로그인 여부 체크
        const ok = requireLogin();
        if (!ok) return;

        if (btnFavorite.dataset.loading === "true") return;
        btnFavorite.dataset.loading = "true";
        btnFavorite.disabled = true;


        try {
            const currentlyFavorited = btnFavorite.classList.contains("active");
            const nextFavorited = !currentlyFavorited;
            // 찜 추가
            const res = await apiRequest(`/api/favorites/${selectedRoomId}`, {
              method: nextFavorited ? "POST" : "DELETE",
            });
            if (!res.ok  && !(res.status === 404 && !nextFavorited)) {
              throw new Error("favorite failed");
            }
          applyFavoriteButtonState(btnFavorite, nextFavorited);
        } catch (e) {
          console.error("[rooms-map] favorite toggle error:", e);
          alert("관심 설정 중 오류가 발생했습니다.");
        } finally {
           btnFavorite.dataset.loading = "false";
           btnFavorite.disabled = false;
        }
      });
    }
  // 문의하기 (나중에 채팅방 생성 API 연동)
    const btnChat = document.getElementById("btn-chat");
    if (btnChat) {
      btnChat.addEventListener("click", async () => {
        if (!selectedRoomId) return;

        const ok = requireLogin();
        if (!ok) return;

        alert(
          "문의하기(채팅)는 추후 WebSocket/채팅 API 연동 예정입니다. (roomId=" +
            selectedRoomId +
            ")"
        );
        // TODO: 나중에 채팅방 생성 API 연동
      });
    }
});

// ====== apiRequest 기반 비즈니스 로직 ======

async function fetchRoomsForCurrentBounds() {
  const bounds = map.getBounds();
  const sw = bounds.getSouthWest();
  const ne = bounds.getNorthEast();

  const south = sw.getLat();
  const west = sw.getLng();
  const north = ne.getLat();
  const east = ne.getLng();
  const zoom = map.getLevel();

  const url = `/api/rooms/map?north=${north}&south=${south}&east=${east}&west=${west}&zoom=${zoom}`;

  try {
    const res = await apiRequest(url, { method: "GET" });

    if (!res.ok) {
      console.error("지도용 방 목록 조회 실패:", res.status);
      return;
    }

    const data = await res.json();
    currentRooms = data;
    renderRoomMarkers(data);
  } catch (e) {
    console.error("지도용 방 목록 조회 중 오류:", e);
  }
}
// 현재 로그인한 사용자 ID 조회 (비로그인 시 null)
async function fetchCurrentMemberId() {
  try {
    const res = await apiRequest("/api/members/me", { method: "GET" });

    if (!res.ok) {
      // 401이면 비로그인
      console.debug("[rooms-map] not logged in (status=", res.status, ")");
      return null;
    }

    const data = await res.json();
    const memberId = data.memberId ?? data.member_id ?? null;
    console.debug("[rooms-map] currentMemberId =", memberId);
    return memberId;
  } catch (e) {
    console.error("[rooms-map] fetchCurrentMemberId error:", e);
    return null;
  }
}

function renderRoomMarkers(rooms) {
  if (markers.length > 0) clusterer.removeMarkers(markers);
  markers = [];
  clusterer.clear();

  rooms.forEach((room) => {
    const lat = room.lat ?? room.roomLat ?? room.room_lat ?? room.latitude;
    const lng = room.lng ?? room.roomLng ?? room.room_lng ?? room.longitude;
    if (lat == null || lng == null) return;

    const roomIdRaw = room.roomId ?? room.room_id;
    const roomId = normalizeRoomId(roomIdRaw);
    if (!roomId) return;
    const pos = new kakao.maps.LatLng(lat, lng);
    const marker = new kakao.maps.Marker({ position: pos });

    kakao.maps.event.addListener(marker, "click", () => {
      selectedRoomId = roomId;
      loadRoomDetail(roomId);
    });

    markers.push(marker);
  });

  clusterer.addMarkers(markers);
}

async function loadRoomDetail(roomId) {
  // ★ 안전장치: 잘못된 roomId일 경우 API 호출 막기
  roomId = normalizeRoomId(roomId);
  if (!roomId) {
    console.warn("[rooms-map] loadRoomDetail 호출 시 roomId 없음:", roomId);
    return;
  }

  // ✅ 마커 클릭 시 상세 카드 전체 보이기
  const detailCard = document.getElementById("room-detail-card");
  if (detailCard) {
    detailCard.style.display = "flex"; // .room-card가 flex 기준이라 flex로
  }

  // (원하면 로딩 문구를 잠깐 보여줄 수도 있음)
  const emptyEl = document.getElementById("room-detail-empty");
  if (emptyEl) {
    emptyEl.style.display = "block";
  }

  try {
    const res = await apiRequest(`/api/rooms/${roomId}`, { method: "GET" });
    console.log("[rooms-map] /api/rooms/" + roomId + " status =", res.status);

    if (!res.ok) {
      console.error("방 상세 조회 실패:", res.status);
      return;
    }

    const room = await res.json();
    renderRoomDetail(room);
    renderNearbyRooms(room);
  } catch (e) {
    console.error("방 상세 조회 중 오류:", e);
  }
}

function renderRoomDetail(room) {
  document.getElementById("room-detail-empty").style.display = "none";
  document.getElementById("room-detail-body").style.display = "block";
  document.getElementById("room-detail-footer").style.display = "block";

  // 찜 버튼 초기 상태 세팅
  const btnFavorite = document.getElementById("btn-favorite");
  const btnChat = document.getElementById("btn-chat");

  const imageUrls = room.imageUrls ?? room.image_urls ?? [];
  const validUrls = imageUrls.filter(Boolean);

  const imgEl = document.getElementById("room-image");
  if (imgEl) {
    if (validUrls.length > 0) {
      imgEl.src = validUrls[0];
      imgEl.style.display = "block";
    } else {
      imgEl.src = "";
      imgEl.style.display = "none";
    }
  }
  document.getElementById("room-title").innerText =
    room.title || "제목 없음";
  document.getElementById("room-address").innerText =
    room.address || "";

  //  월세 / 보증금 (여기 전부 수정됨)
  const monthlyRent = room.monthlyRent ?? room.monthly_rent;
  const deposit = room.deposit;

  let monthlyText;
  if (monthlyRent != null) {
    const manwon = Math.round(monthlyRent / 10000); // 450000 → 45
    monthlyText = `월세 ${formatNumber(manwon)}만원`;
  } else {
    monthlyText = "월세 정보 없음";
  }

  document.getElementById("room-price").innerText = monthlyText;

  //  입주일 / 최대 인원 / 층·면적
  const availableFrom = room.availableFrom ?? room.available_from;
  const maxRoommates = room.maxRoommates ?? room.max_roommates;
  const floor = room.floor;
  const area = room.areaM2 ?? room.area_m2;

  document.getElementById("room-available-from").innerText =
    availableFrom || "협의 가능";

  document.getElementById("room-max-roommates").innerText =
    maxRoommates != null ? `${maxRoommates}명` : "-";

  document.getElementById("room-floor-area").innerText =
    `${floor != null ? floor + "층" : "?층"} · ${
      area != null ? area + "㎡" : "?㎡"
    }`;

  // 설명
  const content = room.content;
  document.getElementById("room-content").innerText =
    cutText(content || "", 120);

  //  조회수 / 상태
  const views = room.views;
  const status = room.status;

  document.getElementById("room-views").innerText =
    views != null ? views : 0;

  const statusTextEl = document.getElementById("room-status-text");
  if (status === "OPEN") {
    statusTextEl.innerText = "모집중";
    statusTextEl.style.color = "#16a34a";
  } else if (status === "RESERVED") {
    statusTextEl.innerText = "예약중";
    statusTextEl.style.color = "#f97316";
  } else if (status === "CLOSED") {
    statusTextEl.innerText = "마감";
    statusTextEl.style.color = "#9ca3af";
  } else {
    statusTextEl.innerText = "";
    statusTextEl.style.color = "#6b7280";
  }

  //  태그(mock)
  const chipContainer = document.getElementById("room-chips");
    const ownerTags = room.ownerTags ?? room.owner_tags ?? [];

    renderChips(chipContainer, ownerTags, { max: 4 });

  // 내가 올린 방이면 찜/문의 버튼 숨기기
  const ownerId = room.ownerId ?? room.owner_id;

  if (ownerId && currentMemberId && String(ownerId) === String(currentMemberId)) {
    // 내 방인 경우 → 찜/문의 숨김
    if (btnFavorite) btnFavorite.style.display = "none";
    if (btnChat) btnChat.style.display = "none";
  } else {
    if (btnFavorite) {
      btnFavorite.style.display = "inline-flex";
      const initialFavorited = !!(room.favorited ?? room.isFavorited);
      applyFavoriteButtonState(btnFavorite, initialFavorited);
    }
    if (btnChat) {
      btnChat.style.display = "inline-flex";
    }
  }
}

function renderNearbyRooms(selectedRoom) {
  const listEl = document.getElementById("nearby-list");
  listEl.innerHTML = "";
  const sLat = selectedRoom.lat ?? selectedRoom.roomLat ?? selectedRoom.room_lat ?? selectedRoom.latitude;
  const sLng = selectedRoom.lng ?? selectedRoom.roomLng ?? selectedRoom.room_lng ?? selectedRoom.longitude;

  if (!selectedRoom || sLat == null || sLng == null || !currentRooms.length) {
    const p = document.createElement("p");
    p.className = "room-nearby-empty";
    p.innerText = "근처 매물이 없습니다.";
    listEl.appendChild(p);
    return;
  }
  const selectedId = normalizeRoomId(selectedRoom.roomId ?? selectedRoom.room_id);

  // 간단 거리 계산 (위도/경도 차이로 근사)
  function distance(room) {
    const rLat = room.lat ?? room.roomLat ?? room.room_lat ?? room.latitude;
    const rLng = room.lng ?? room.roomLng ?? room.room_lng ?? room.longitude;
    if (rLat == null || rLng == null) return Infinity;
    const dx = sLat - rLat;
    const dy = sLng - rLng;
    return Math.sqrt(dx * dx + dy * dy);
  }

  // 현재 뷰포트 안 방들 중에서, 자기 자신을 제외하고 가까운 순으로 3개
  const candidates = currentRooms
    .filter((r) => {
      const id = normalizeRoomId(r.roomId ?? r.room_id);
      return id && id !== selectedId;
    })
    .map((r) => ({ room: r, dist: distance(r) }))
    .filter((x) => x.dist < Infinity)
    .sort((a, b) => a.dist - b.dist)
    .slice(0, 3); // 상위 3개만

  if (candidates.length === 0) {
    const p = document.createElement("p");
    p.className = "room-nearby-empty";
    p.innerText = "근처 매물이 없습니다.";
    listEl.appendChild(p);
    return;
  }

  candidates.forEach(({ room }) => {
    const id = room.roomId ?? room.room_id;
    const title = room.title ?? room.room_title ?? "제목 없음";
    const addr = room.address ?? "";
    const monthlyRent = room.monthlyRent ?? room.monthly_rent;
    const thumbnail = room.thumbnailUrl ?? room.thumbnail_url ?? "";
    const manwon = monthlyRent != null ? Math.round(monthlyRent / 10000) : null;

    const item = document.createElement("div");
    item.className = "room-nearby-item";
    item.addEventListener("click", () => {
      // 리스트 아이템 클릭 → 그 방 상세로 이동
      selectedRoomId = id;
      loadRoomDetail(id);
    const rLat = room.lat ?? room.roomLat ?? room.room_lat ?? room.latitude;
    const rLng = room.lng ?? room.roomLng ?? room.room_lng ?? room.longitude;

      if (rLat != null && rLng != null && map) {
        map.setCenter(new kakao.maps.LatLng(rLat, rLng));
      }
    });

    const img = document.createElement("img");
    img.className = "room-nearby-thumb";
    img.src = thumbnail || "/resources/img/room/default-room.jpg";
    img.alt = "room image";

    const meta = document.createElement("div");
    meta.className = "room-nearby-meta";

    const titleEl = document.createElement("div");
    titleEl.className = "room-nearby-title-text";
    titleEl.innerText = cutText(title, 18);

    const addrEl = document.createElement("div");
    addrEl.className = "room-nearby-address";
    addrEl.innerText = addr;

    const priceEl = document.createElement("div");
    priceEl.className = "room-nearby-price";
    priceEl.innerText =
      manwon != null ? `월세 ${formatNumber(manwon)}만원` : "월세 정보 없음";

    meta.appendChild(titleEl);
    meta.appendChild(addrEl);
    meta.appendChild(priceEl);

    item.appendChild(img);
    item.appendChild(meta);
    listEl.appendChild(item);
  });
}

function applyFavoriteButtonState(btn, favorited) {
  if (!btn) return;
  if (favorited) {
    btn.classList.add("active");
    btn.textContent = "찜 완료";
  } else {
    btn.classList.remove("active");
    btn.textContent = "♡ 찜하기";
  }
}
function renderChips(containerEl, tags, options = {}) {
  const { max = 6, emptyText = null } = options;

  if (!containerEl) return;

  containerEl.innerHTML = "";

  const safeTags = Array.isArray(tags) ? tags.filter(Boolean) : [];
  const sliced = safeTags.slice(0, max);

  if (sliced.length === 0) {
    if (emptyText) {
      const span = document.createElement("span");
      span.className = "chip";
      span.innerText = emptyText;
      containerEl.appendChild(span);
    }
    return;
  }

  sliced.forEach((text) => {
    const span = document.createElement("span");
    //  CSS가 어디에 정의돼있든 최대한 깨지지 않게 두 클래스 같이 줌
    span.className = "chip room-chip";
    span.innerText = text;
    containerEl.appendChild(span);
  });
}


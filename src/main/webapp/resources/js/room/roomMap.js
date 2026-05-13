import { apiRequest } from "../common/apiClient.js";
import { requireLogin } from "../common/authGuard.js";
import { getAccessToken, getTokenType } from "../common/authTokenStorage.js";

const contextPath = window.contextPath || "";
const searchParams = new URLSearchParams(window.location.search);
const hasSearch = Array.from(searchParams.keys()).length > 0;

let map;
let clusterer;
let selectedRoomId = null;
let markers = [];
let currentRooms = [];
let currentMemberId = null;

window.addEventListener("load", async () => {
  if (!window.kakao?.maps) {
    showMapError("지도를 불러오지 못했습니다. Kakao 지도 키와 네트워크 상태를 확인해 주세요.");
    return;
  }

  const container = document.getElementById("map");
  if (!container) return;

  map = new kakao.maps.Map(container, {
    center: new kakao.maps.LatLng(37.5665, 126.9780),
    level: 6,
  });
  map.relayout();

  clusterer = new kakao.maps.MarkerClusterer({
    map,
    averageCenter: true,
    minLevel: 7,
  });

  bindMapUi();
  currentMemberId = await fetchCurrentMemberId();
  await fetchRoomsForCurrentBounds();

  let idleTimer = null;
  kakao.maps.event.addListener(map, "idle", () => {
    if (hasSearch) return;
    clearTimeout(idleTimer);
    idleTimer = setTimeout(fetchRoomsForCurrentBounds, 300);
  });
});

function bindMapUi() {
  document.getElementById("room-close-btn")?.addEventListener("click", () => {
    selectedRoomId = null;
    setDetailVisible(false);
  });

  document.getElementById("btn-detail")?.addEventListener("click", () => {
    if (!selectedRoomId) return;
    window.location.href = `${contextPath}/rooms/${encodeURIComponent(selectedRoomId)}`;
  });

  document.getElementById("btn-favorite")?.addEventListener("click", toggleFavorite);
  document.getElementById("btn-chat")?.addEventListener("click", openChatRoom);
}

async function fetchRoomsForCurrentBounds() {
  const bounds = getRequestBounds();
  const url = `${contextPath}/api/rooms/map?north=${bounds.north}&south=${bounds.south}&east=${bounds.east}&west=${bounds.west}&zoom=${bounds.zoom}`;

  try {
    const response = await apiRequest(url, { method: "GET" });
    if (!response.ok) throw new Error(`rooms map api failed: ${response.status}`);

    const rooms = await response.json();
    const allowedRoomIds = await fetchAllowedRoomIdsFromRecommendation();
    currentRooms = applySearchFilters(rooms, allowedRoomIds);

    renderRoomMarkers(currentRooms);
    renderSearchResultList(currentRooms);
    focusSearchResults(currentRooms);
  } catch (error) {
    console.error(error);
    renderSearchResultList([]);
  }
}

function getRequestBounds() {
  if (hasSearch) {
    return { south: 33, north: 39, west: 124, east: 132, zoom: 6 };
  }

  const bounds = map.getBounds();
  const sw = bounds.getSouthWest();
  const ne = bounds.getNorthEast();
  return {
    south: sw.getLat(),
    west: sw.getLng(),
    north: ne.getLat(),
    east: ne.getLng(),
    zoom: map.getLevel(),
  };
}

async function fetchAllowedRoomIdsFromRecommendation() {
  const recommendationKeys = ["gender", "work_type_id", "hobby_id", "preference_id", "pet_id"];
  const needsRecommendationFilter = recommendationKeys.some((key) => searchParams.has(key));
  if (!needsRecommendationFilter) return null;

  const response = await fetch(`${contextPath}/api/members/recommended-roommates?${searchParams.toString()}`, {
    method: "GET",
    headers: { Accept: "application/json" },
  });
  if (!response.ok) throw new Error(`recommended roommates api failed: ${response.status}`);

  const roommates = await response.json();
  return new Set(roommates.map((item) => String(item.room_id ?? item.roomId)).filter(Boolean));
}

function applySearchFilters(rooms, allowedRoomIds) {
  if (!Array.isArray(rooms)) return [];

  const region = searchParams.get("region");
  const budget = searchParams.get("budget");

  return rooms.filter((room) => {
    const roomId = String(room.room_id ?? room.roomId ?? "");
    const address = `${room.address ?? ""} ${room.legal_dong ?? room.legalDong ?? ""}`;
    const monthlyRent = rentInManwon(room.monthly_rent ?? room.monthlyRent);

    if (allowedRoomIds && !allowedRoomIds.has(roomId)) return false;
    if (region && !address.includes(region)) return false;
    if (budget && monthlyRent != null && monthlyRent > Number(budget)) return false;
    return true;
  });
}

function renderRoomMarkers(rooms) {
  if (markers.length > 0) clusterer.removeMarkers(markers);
  markers = [];
  clusterer.clear();

  rooms.forEach((room) => {
    const lat = room.lat;
    const lng = room.lng;
    const roomId = normalizeRoomId(room.room_id ?? room.roomId);
    if (lat == null || lng == null || !roomId) return;

    const marker = new kakao.maps.Marker({
      position: new kakao.maps.LatLng(lat, lng),
    });

    kakao.maps.event.addListener(marker, "click", () => {
      selectedRoomId = roomId;
      loadRoomDetail(roomId);
    });

    markers.push(marker);
  });

  clusterer.addMarkers(markers);
}

function renderSearchResultList(rooms) {
  const title = document.getElementById("nearby-title");
  const list = document.getElementById("nearby-list");
  if (!list) return;

  if (title) title.textContent = hasSearch ? "검색 조건 매물" : "근처 다른 매물";
  list.innerHTML = "";

  if (!Array.isArray(rooms) || rooms.length === 0) {
    list.innerHTML = '<p class="room-nearby-empty">조건에 맞는 매물이 없습니다.</p>';
    return;
  }

  rooms.forEach((room) => {
    list.appendChild(createRoomListItem(room));
  });
}

function createRoomListItem(room) {
  const roomId = normalizeRoomId(room.room_id ?? room.roomId);
  const title = room.room_title ?? room.roomTitle ?? "제목 없음";
  const address = room.address ?? "";
  const thumbnail = room.thumbnail_url ?? room.thumbnailUrl ?? "";
  const rent = rentInManwon(room.monthly_rent ?? room.monthlyRent);

  const item = document.createElement("div");
  item.className = "room-nearby-item";
  item.addEventListener("click", () => {
    if (!roomId) return;
    selectedRoomId = roomId;
    loadRoomDetail(roomId);
    if (room.lat != null && room.lng != null && map) {
      map.setCenter(new kakao.maps.LatLng(room.lat, room.lng));
    }
  });

  item.innerHTML = `
    <img class="room-nearby-thumb" src="${escapeAttribute(thumbnail || `${contextPath}/resources/img/room/default-room.jpg`)}" alt="방 이미지">
    <div class="room-nearby-meta">
      <div class="room-nearby-title-text">${escapeHtml(cutText(title, 22))}</div>
      <div class="room-nearby-address">${escapeHtml(address)}</div>
      <div class="room-nearby-price">${rent != null ? `월세 ${formatNumber(rent)}만원` : "월세 정보 없음"}</div>
    </div>
  `;
  return item;
}

function focusSearchResults(rooms) {
  if (!hasSearch || !Array.isArray(rooms) || rooms.length === 0 || !map) return;

  const bounds = new kakao.maps.LatLngBounds();
  let count = 0;
  rooms.forEach((room) => {
    if (room.lat == null || room.lng == null) return;
    bounds.extend(new kakao.maps.LatLng(room.lat, room.lng));
    count += 1;
  });

  if (count === 1) {
    const room = rooms.find((item) => item.lat != null && item.lng != null);
    map.setCenter(new kakao.maps.LatLng(room.lat, room.lng));
    map.setLevel(4);
  } else if (count > 1) {
    map.setBounds(bounds);
  }
}

async function loadRoomDetail(roomId) {
  try {
    const response = await apiRequest(`${contextPath}/api/rooms/${encodeURIComponent(roomId)}`, { method: "GET" });
    if (!response.ok) throw new Error(`room detail api failed: ${response.status}`);

    renderRoomDetail(await response.json());
  } catch (error) {
    console.error(error);
    alert("방 정보를 불러오지 못했습니다.");
  }
}

function renderRoomDetail(room) {
  setDetailVisible(true);

  const imageUrls = room.image_urls ?? room.imageUrls ?? [];
  const firstImage = Array.isArray(imageUrls) ? imageUrls.find(Boolean) : null;
  const image = document.getElementById("room-image");
  if (image) {
    image.src = firstImage || "";
    image.style.display = firstImage ? "block" : "none";
  }

  setText("room-title", room.room_title ?? room.roomTitle ?? "제목 없음");
  setText("room-address", room.address ?? "");
  setText("room-price", formatRoomRent(room.monthly_rent ?? room.monthlyRent));
  setText("room-available-from", room.available_from ?? room.availableFrom ?? "-");
  setText("room-max-roommates", room.max_roommates ?? room.maxRoommates ? `${room.max_roommates ?? room.maxRoommates}명` : "-");
  setText("room-floor-area", `${room.floor ?? "-"}층 / ${room.area_m2 ?? room.areaM2 ?? "-"}㎡`);
  setText("room-content", cutText(room.content ?? "", 120));
  setText("room-views", room.views ?? 0);
  setText("room-status-text", statusText(room.status));

  renderChips(document.getElementById("room-chips"), room.owner_tags ?? room.ownerTags ?? []);

  const ownerId = room.owner_id ?? room.ownerId;
  const favoriteButton = document.getElementById("btn-favorite");
  const chatButton = document.getElementById("btn-chat");

  if (ownerId && currentMemberId && String(ownerId) === String(currentMemberId)) {
    if (favoriteButton) favoriteButton.style.display = "none";
    if (chatButton) chatButton.style.display = "none";
    return;
  }

  if (favoriteButton) {
    favoriteButton.style.display = "inline-flex";
    applyFavoriteButtonState(favoriteButton, Boolean(room.favorited ?? room.isFavorited));
  }
  if (chatButton) {
    chatButton.style.display = "inline-flex";
    chatButton.dataset.partnerId = ownerId || "";
  }
}

async function toggleFavorite() {
  if (!selectedRoomId || !requireLogin()) return;

  const button = document.getElementById("btn-favorite");
  if (!button || button.dataset.loading === "true") return;

  const nextFavorited = !button.classList.contains("active");
  button.dataset.loading = "true";
  button.disabled = true;
  try {
    const response = await apiRequest(`${contextPath}/api/favorites/${encodeURIComponent(selectedRoomId)}`, {
      method: nextFavorited ? "POST" : "DELETE",
    });
    if (!response.ok && !(response.status === 404 && !nextFavorited)) {
      throw new Error(`favorite api failed: ${response.status}`);
    }
    applyFavoriteButtonState(button, nextFavorited);
  } catch (error) {
    console.error(error);
    alert("찜 설정 중 오류가 발생했습니다.");
  } finally {
    button.dataset.loading = "false";
    button.disabled = false;
  }
}

async function openChatRoom() {
  if (!selectedRoomId || !requireLogin()) return;

  const button = document.getElementById("btn-chat");
  button.disabled = true;
  try {
    const response = await apiRequest(`${contextPath}/api/chat/rooms`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
      },
      body: JSON.stringify({
        room_id: Number(selectedRoomId),
      }),
    });
    if (!response.ok) throw new Error(`chat room api failed: ${response.status}`);

    const data = await response.json();
    const chatRoomId = data.chat_room_id ?? data.chatRoomId;
    if (chatRoomId) {
      window.location.href = `${contextPath}/chats/${encodeURIComponent(chatRoomId)}`;
    }
  } catch (error) {
    console.error(error);
    alert("채팅방을 열지 못했습니다.");
  } finally {
    button.disabled = false;
  }
}

async function fetchCurrentMemberId() {
  const token = getAccessToken();
  if (!token) return null;

  try {
    const response = await fetch(`${contextPath}/api/members/me`, {
      method: "GET",
      headers: {
        Accept: "application/json",
        Authorization: `${getTokenType()} ${token}`,
      },
    });
    if (!response.ok) return null;
    const data = await response.json();
    return data.member_id ?? data.memberId ?? null;
  } catch (_) {
    return null;
  }
}

function setDetailVisible(visible) {
  const empty = document.getElementById("room-detail-empty");
  const body = document.getElementById("room-detail-body");
  const footer = document.getElementById("room-detail-footer");
  const image = document.getElementById("room-image");

  if (empty) empty.style.display = visible ? "none" : "block";
  if (body) body.style.display = visible ? "block" : "none";
  if (footer) footer.style.display = visible ? "block" : "none";
  if (!visible && image) image.style.display = "none";
}

function renderChips(container, tags) {
  if (!container) return;
  const safeTags = Array.isArray(tags) ? tags.filter(Boolean).slice(0, 4) : [];
  container.innerHTML = safeTags.map((tag) => `<span class="chip room-chip">${escapeHtml(tag)}</span>`).join("");
}

function applyFavoriteButtonState(button, favorited) {
  button.classList.toggle("active", favorited);
  button.textContent = favorited ? "찜 완료" : "찜하기";
}

function showMapError(message) {
  const mapEl = document.getElementById("map");
  if (mapEl) {
    mapEl.innerHTML = `<div style="padding:24px;color:#667085;">${escapeHtml(message)}</div>`;
  }
}

function normalizeRoomId(value) {
  if (value == null) return null;
  const text = String(value).trim();
  return /^\d+$/.test(text) ? text : null;
}

function rentInManwon(value) {
  if (value == null || value === "") return null;
  const numeric = Number(value);
  if (Number.isNaN(numeric)) return null;
  return numeric > 10000 ? Math.round(numeric / 10000) : numeric;
}

function formatRoomRent(value) {
  const rent = rentInManwon(value);
  return rent != null ? `월세 ${formatNumber(rent)}만원` : "월세 정보 없음";
}

function formatNumber(value) {
  if (value == null) return "-";
  return Number(value).toLocaleString("ko-KR");
}

function statusText(status) {
  if (status === "OPEN") return "모집중";
  if (status === "RESERVED") return "예약중";
  if (status === "CLOSED") return "마감";
  if (status === "HIDDEN") return "비공개";
  return "";
}

function cutText(text, maxLen) {
  const value = String(text ?? "");
  return value.length > maxLen ? `${value.slice(0, maxLen)}...` : value;
}

function setText(id, value) {
  const element = document.getElementById(id);
  if (element) element.textContent = value;
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function escapeAttribute(value) {
  return escapeHtml(value).replaceAll("`", "&#096;");
}

const contextPath = window.contextPath || "";
const broadBounds = {
  south: 33,
  north: 39,
  west: 124,
  east: 132,
  zoom: 6,
};

let allRooms = [];

document.addEventListener("DOMContentLoaded", async () => {
  document.getElementById("roomSearchForm")?.addEventListener("submit", (event) => {
    event.preventDefault();
    renderRooms(applyFilters(allRooms));
  });

  document.getElementById("btnRefreshRooms")?.addEventListener("click", async () => {
    const form = document.getElementById("roomSearchForm");
    form?.reset();
    await loadRooms();
  });

  await loadRooms();
});

async function loadRooms() {
  const params = new URLSearchParams(broadBounds);
  try {
    const response = await fetch(`${contextPath}/api/rooms/map?${params.toString()}`, {
      headers: { Accept: "application/json" },
    });
    if (!response.ok) throw new Error(`rooms api failed: ${response.status}`);
    allRooms = await response.json();
    renderRooms(applyFilters(allRooms));
  } catch (error) {
    console.error(error);
    setCount("매물을 불러오지 못했습니다.");
    document.getElementById("roomList").innerHTML = '<div class="room-list-empty">매물을 불러오지 못했습니다.</div>';
  }
}

function applyFilters(rooms) {
  const form = document.getElementById("roomSearchForm");
  const formData = new FormData(form);
  const region = String(formData.get("region") || "").trim().toLowerCase();
  const budget = Number(formData.get("budget") || 0);
  const sort = formData.get("sort") || "latest";

  const filtered = rooms.filter((room) => {
    const address = String(room.address || "").toLowerCase();
    const title = String(room.room_title ?? room.roomTitle ?? "").toLowerCase();
    const rent = Number(room.monthly_rent ?? room.monthlyRent ?? 0);
    if (region && !address.includes(region) && !title.includes(region)) return false;
    if (budget > 0 && rent > budget) return false;
    return true;
  });

  filtered.sort((a, b) => {
    const rentA = Number(a.monthly_rent ?? a.monthlyRent ?? 0);
    const rentB = Number(b.monthly_rent ?? b.monthlyRent ?? 0);
    const idA = Number(a.room_id ?? a.roomId ?? 0);
    const idB = Number(b.room_id ?? b.roomId ?? 0);
    if (sort === "rentAsc") return rentA - rentB;
    if (sort === "rentDesc") return rentB - rentA;
    return idB - idA;
  });

  return filtered;
}

function renderRooms(rooms) {
  const container = document.getElementById("roomList");
  if (!container) return;

  setCount(`${rooms.length.toLocaleString("ko-KR")}개의 매물`);
  if (!Array.isArray(rooms) || rooms.length === 0) {
    container.innerHTML = '<div class="room-list-empty">조건에 맞는 매물이 없습니다.</div>';
    return;
  }

  container.innerHTML = rooms.map(renderRoomCard).join("");
  container.querySelectorAll(".room-card").forEach((card) => {
    card.addEventListener("click", () => {
      window.location.href = `${contextPath}/rooms/${encodeURIComponent(card.dataset.roomId)}`;
    });
  });
}

function renderRoomCard(room) {
  const roomId = room.room_id ?? room.roomId;
  const title = room.room_title ?? room.roomTitle ?? "제목 없음";
  const address = room.address || "주소 미등록";
  const image = room.thumbnail_url ?? room.thumbnailUrl ?? `${contextPath}/resources/img/default-room.svg`;
  const rent = formatMoney(room.monthly_rent ?? room.monthlyRent);
  const deposit = formatMoney(room.deposit);
  const status = room.status || "OPEN";

  return `
    <article class="room-card" data-room-id="${escapeAttribute(roomId)}">
      <img src="${escapeAttribute(image)}" alt="${escapeAttribute(title)}" onerror="this.src='${contextPath}/resources/img/default-room.svg'">
      <div class="room-card-body">
        <h2 class="room-card-title">${escapeHtml(title)}</h2>
        <p class="room-card-address">${escapeHtml(address)}</p>
        <p class="room-card-price">월세 ${escapeHtml(rent)} / 보증금 ${escapeHtml(deposit)}</p>
        <span class="room-status">${escapeHtml(statusText(status))}</span>
      </div>
    </article>
  `;
}

function setCount(text) {
  const count = document.getElementById("roomListCount");
  if (count) count.textContent = text;
}

function statusText(status) {
  if (status === "OPEN") return "모집중";
  if (status === "RESERVED") return "예약중";
  return status;
}

function formatMoney(value) {
  if (value == null || value === "") return "-";
  const number = Number(value);
  if (Number.isNaN(number)) return "-";
  return number.toLocaleString("ko-KR");
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function escapeAttribute(value) {
  return escapeHtml(value);
}

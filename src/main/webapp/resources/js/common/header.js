import {
  getAccessToken,
  clearTokens,
} from "../common/authTokenStorage.js";
import { apiRequest } from "../common/apiClient.js";

document.addEventListener("DOMContentLoaded", async () => {
  const authButtons = document.getElementById("headerAuthButtons");
  const profileArea = document.getElementById("headerProfileArea");
  const headerUsername = document.getElementById("headerUsername");
  const btnLogout = document.getElementById("btnLogout");
  const btnOpenLogin = document.getElementById("btnOpenLogin");
  const btnOpenRegister = document.getElementById("btnOpenRegister");
  const btnHeaderRoomCreate = document.getElementById("btnHeaderRoomCreate");
  const headerAdminLink = document.getElementById("headerAdminLink");
  const btnHeaderNotifications = document.getElementById("btnHeaderNotifications");
  const headerNotificationBadge = document.getElementById("headerNotificationBadge");
  const headerNotificationPanel = document.getElementById("headerNotificationPanel");
  const headerNotificationList = document.getElementById("headerNotificationList");

  btnOpenLogin?.addEventListener("click", () => {
    window.openAuthModal?.("login");
  });

  btnOpenRegister?.addEventListener("click", () => {
    window.openAuthModal?.("register");
  });

  btnLogout?.addEventListener("click", async () => {
    if (!confirm("로그아웃 하시겠습니까?")) return;

    try {
      await apiRequest("/api/auth/logout", { method: "POST" });
    } catch (error) {
      console.error(error);
    } finally {
      clearTokens();
      location.reload();
    }
  });

  const showGuestHeader = () => {
    if (btnHeaderRoomCreate) btnHeaderRoomCreate.style.display = "none";
    if (headerAdminLink) headerAdminLink.style.display = "none";
    if (headerNotificationPanel) headerNotificationPanel.style.display = "none";
    if (authButtons) authButtons.style.display = "flex";
    if (profileArea) profileArea.style.display = "none";
  };

  const token = getAccessToken();
  if (!token) {
    showGuestHeader();
    return;
  }

  try {
    const res = await apiRequest("/api/members/me", { method: "GET" });
    if (res.status === 401 || res.status === 403) {
      clearTokens();
      showGuestHeader();
      return;
    }
    if (!res.ok) {
      console.warn("me api failed:", res.status);
      showGuestHeader();
      return;
    }

    const data = await res.json();
    const name = data.name || "";
    const email = data.email || "";
    const role = data.member_role_enum || data.role || "";
    const isAdmin = role === "ADMIN" || role === "SUPER_ADMIN";
    const handle = email && email.includes("@") ? email.split("@")[0] : name || "user";

    if (headerUsername) headerUsername.textContent = handle;
    if (authButtons) authButtons.style.display = "none";
    if (profileArea) profileArea.style.display = "flex";
    if (btnHeaderRoomCreate) btnHeaderRoomCreate.style.display = "inline-flex";
    if (headerAdminLink) headerAdminLink.style.display = isAdmin ? "inline-flex" : "none";
    bindNotifications();
    await loadUnreadNotificationCount();
  } catch (error) {
    console.warn("header init error:", error);
    showGuestHeader();
  }

  function bindNotifications() {
    if (!btnHeaderNotifications || !headerNotificationPanel) return;
    btnHeaderNotifications.addEventListener("click", async (event) => {
      event.stopPropagation();
      const willOpen = headerNotificationPanel.style.display === "none";
      headerNotificationPanel.style.display = willOpen ? "block" : "none";
      btnHeaderNotifications.setAttribute("aria-expanded", String(willOpen));
      if (willOpen) await loadRecentNotifications();
    });

    document.addEventListener("click", (event) => {
      if (!headerNotificationPanel || !btnHeaderNotifications) return;
      if (headerNotificationPanel.contains(event.target) || btnHeaderNotifications.contains(event.target)) return;
      headerNotificationPanel.style.display = "none";
      btnHeaderNotifications.setAttribute("aria-expanded", "false");
    });
  }

  async function loadUnreadNotificationCount() {
    if (!headerNotificationBadge) return;
    try {
      const res = await apiRequest("/api/notifications/unread-count", { method: "GET" });
      if (!res.ok) return;
      const data = await res.json();
      const count = Number(data.unread_count || 0);
      headerNotificationBadge.textContent = count > 99 ? "99+" : String(count);
      headerNotificationBadge.style.display = count > 0 ? "inline-flex" : "none";
    } catch (error) {
      console.warn("notification count failed:", error);
    }
  }

  async function loadRecentNotifications() {
    if (!headerNotificationList) return;
    headerNotificationList.innerHTML = '<p class="header-notification-empty">알림을 불러오는 중입니다.</p>';
    try {
      const res = await apiRequest("/api/notifications?limit=5", { method: "GET" });
      if (!res.ok) throw new Error("notification list failed: " + res.status);
      const notifications = await res.json();
      if (!Array.isArray(notifications) || notifications.length === 0) {
        headerNotificationList.innerHTML = '<p class="header-notification-empty">알림이 없습니다.</p>';
        return;
      }
      headerNotificationList.innerHTML = notifications.map(renderNotification).join("");
      headerNotificationList.querySelectorAll("[data-notification-id]").forEach((item) => {
        item.addEventListener("click", async () => {
          await handleNotificationClick(item);
        });
      });
    } catch (error) {
      console.warn("notification list failed:", error);
      headerNotificationList.innerHTML = '<p class="header-notification-empty">알림을 불러오지 못했습니다.</p>';
    }
  }

  async function handleNotificationClick(item) {
    const notificationId = item.dataset.notificationId;
    const notificationType = item.dataset.notificationType;
    const referenceId = item.dataset.referenceId;
    const notificationMessage = item.dataset.notificationMessage || "";

    if (notificationId) {
      await apiRequest(`/api/notifications/${encodeURIComponent(notificationId)}/read`, { method: "PATCH" });
    }
    item.remove();
    await loadUnreadNotificationCount();

    if (notificationType === "REPORT" && referenceId) {
      try {
        const detailRes = await apiRequest(`/api/reports/me/${encodeURIComponent(referenceId)}`, { method: "GET" });
        if (detailRes.ok) {
          openReportNotificationDialog(await detailRes.json());
        } else {
          openReportNotificationDialog(parseReportNotificationMessage(referenceId, notificationMessage));
        }
      } catch (error) {
        console.warn("report notification detail failed:", error);
        openReportNotificationDialog(parseReportNotificationMessage(referenceId, notificationMessage));
      }
      await loadRecentNotifications();
      return;
    }
    if (notificationType === "CHAT" && referenceId) {
      window.location.href = `/chats/${encodeURIComponent(referenceId)}`;
      return;
    }
    if (notificationType === "ROOM" && referenceId) {
      window.location.href = `/rooms/${encodeURIComponent(referenceId)}`;
      return;
    }

    await loadRecentNotifications();
  }
});

function renderNotification(notification) {
  const isUnread = !notification.is_read && notification.is_read !== true;
  return `
    <button
      type="button"
      class="header-notification-item ${isUnread ? "is-unread" : ""}"
      data-notification-id="${escapeAttribute(notification.notification_id)}"
      data-notification-type="${escapeAttribute(notification.type)}"
      data-reference-id="${escapeAttribute(notification.reference_id)}"
      data-notification-message="${escapeAttribute(notification.message || "")}">
      <p class="header-notification-message">${escapeHtml(notification.message || "-")}</p>
      <p class="header-notification-meta">${escapeHtml(notificationTypeText(notification.type))} · ${escapeHtml(formatNotificationDate(notification.created_at))}</p>
    </button>
  `;
}

function openReportNotificationDialog(report) {
  let overlay = document.getElementById("reportNotificationDialog");
  if (!overlay) {
    overlay = document.createElement("div");
    overlay.id = "reportNotificationDialog";
    overlay.className = "report-notification-dialog";
    overlay.innerHTML = `
      <div class="report-notification-dialog__panel" role="dialog" aria-modal="true" aria-labelledby="reportNotificationTitle">
        <div class="report-notification-dialog__header">
          <h2 id="reportNotificationTitle">신고 처리 결과</h2>
          <button type="button" class="report-notification-dialog__close" data-close-report-notification aria-label="닫기">x</button>
        </div>
        <div class="report-notification-dialog__body" id="reportNotificationBody"></div>
        <div class="report-notification-dialog__actions">
          <button type="button" class="header-text-btn" data-close-report-notification>확인</button>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);
    overlay.addEventListener("click", (event) => {
      if (event.target === overlay || event.target.closest("[data-close-report-notification]")) {
        overlay.style.display = "none";
      }
    });
  }

  const resultText = resolutionTypeText(report.resolution_type);
  const resultClass = report.resolution_type === "REJECTED" ? "is-rejected" : "is-accepted";
  const message = report.resolution_message || report.message || "처리 안내 메시지가 없습니다.";
  const body = overlay.querySelector("#reportNotificationBody");
  if (body) {
    body.innerHTML = `
      <div class="report-notification-dialog__status-row">
        <span class="report-notification-dialog__eyebrow">처리 완료</span>
        <span class="report-notification-dialog__badge ${resultClass}">${escapeHtml(resultText)}</span>
      </div>
      <p class="report-notification-dialog__summary">신고 처리가 완료되었습니다.</p>
      <dl class="report-notification-dialog__meta">
        <div>
          <dt>신고 ID</dt>
          <dd>#${escapeHtml(report.report_id || "-")}</dd>
        </div>
        <div>
          <dt>신고 유형</dt>
          <dd>${escapeHtml(reportTypeText(report.report_type))}</dd>
        </div>
        <div>
          <dt>처리일</dt>
          <dd>${escapeHtml(formatFullDate(report.processed_at) || "-")}</dd>
        </div>
      </dl>
      <div class="report-notification-dialog__message-label">관리자 답변</div>
      <div class="report-notification-dialog__message">
        ${escapeHtml(message)}
      </div>
    `;
  }
  overlay.style.display = "flex";
}

function parseReportNotificationMessage(reportId, message) {
  const text = String(message || "").trim();
  let resolutionType = "";
  let resolutionMessage = text;

  if (text.includes("반려")) {
    resolutionType = "REJECTED";
    resolutionMessage = text.replace(/^신고가\s*반려되었습니다\.?\s*/u, "").trim();
  } else if (text.includes("인정")) {
    resolutionType = "ACCEPTED";
    resolutionMessage = text.replace(/^신고가\s*인정되었습니다\.?\s*/u, "").trim();
  }

  return {
    report_id: reportId,
    resolution_type: resolutionType,
    resolution_message: resolutionMessage || text,
  };
}

function resolutionTypeText(value) {
  if (value === "ACCEPTED") return "신고 인정";
  if (value === "REJECTED") return "신고 반려";
  if (value === "NO_ACTION") return "조치 없음";
  return "처리 완료";
}

function reportTypeText(value) {
  if (value === "MEMBER") return "회원";
  if (value === "ROOM") return "매물";
  if (value === "CHAT") return "채팅";
  return value || "-";
}

function notificationTypeText(type) {
  if (type === "REPORT") return "신고 답변";
  if (type === "CHAT") return "채팅";
  if (type === "ROOM") return "방";
  if (type === "SYSTEM") return "시스템";
  return type || "알림";
}

function formatNotificationDate(value) {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return String(value);
  return date.toLocaleString("ko-KR", {
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function formatFullDate(value) {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return String(value);
  return date.toLocaleString("ko-KR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
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

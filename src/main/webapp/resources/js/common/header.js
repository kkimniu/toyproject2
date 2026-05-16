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

  btnOpenLogin?.addEventListener("click", () => {
    window.openAuthModal?.("login");
  });

  btnOpenRegister?.addEventListener("click", () => {
    window.openAuthModal?.("register");
  });

  btnLogout?.addEventListener("click", async () => {
    const ok = confirm("로그아웃 하시겠습니까?");
    if (!ok) return;

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
    const isAdmin = data.member_role_enum === "ADMIN";
    const handle = email && email.includes("@") ? email.split("@")[0] : name || "user";

    if (headerUsername) headerUsername.textContent = handle;
    if (authButtons) authButtons.style.display = "none";
    if (profileArea) profileArea.style.display = "flex";
    if (btnHeaderRoomCreate) btnHeaderRoomCreate.style.display = "inline-flex";
    if (headerAdminLink) headerAdminLink.style.display = isAdmin ? "inline-flex" : "none";
  } catch (error) {
    console.warn("header init error:", error);
    showGuestHeader();
  }
});

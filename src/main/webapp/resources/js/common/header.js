import {
  getAccessToken,
  clearTokens,
} from "../common/authTokenStorage.js";
import { apiRequest } from "../common/apiClient.js";

document.addEventListener("DOMContentLoaded", async () => {
  const authButtons   = document.getElementById("headerAuthButtons");
  const profileArea   = document.getElementById("headerProfileArea");
  const headerUsername = document.getElementById("headerUsername");
  const btnLogout     = document.getElementById("btnLogout");
  const btnOpenLogin  = document.getElementById("btnOpenLogin");
  const btnOpenRegister = document.getElementById("btnOpenRegister");
  const btnHeaderRoomCreate = document.getElementById("btnHeaderRoomCreate");

  // 로그인/회원가입 버튼 클릭 → 모달 열기
  if (btnOpenLogin) {
    btnOpenLogin.addEventListener("click", () => {
      window.openAuthModal && window.openAuthModal("login");
    });
  }
  if (btnOpenRegister) {
    btnOpenRegister.addEventListener("click", () => {
      window.openAuthModal && window.openAuthModal("register");
    });
  }

  const token = getAccessToken();
  if (!token) {
    // 비로그인
    if (btnHeaderRoomCreate) btnHeaderRoomCreate.style.display = "none";
    authButtons && (authButtons.style.display = "flex");
    profileArea && (profileArea.style.display = "none");
    return;
  }

  try {
    const res = await apiRequest("/api/members/me", { method: "GET" });
    if (res.status === 401 || res.status === 403) {
      throw new Error("AUTH");
    }
    // 그 외 에러(500 등)는 로그아웃까지는 하지 말자
    if (!res.ok) {
      console.warn("me api failed:", res.status);
     // 토큰을 지우진 않되, 화면은 비로그인처럼 보여주는게 더 안전
     authButtons && (authButtons.style.display = "flex");
     profileArea && (profileArea.style.display = "none");
     if (btnHeaderRoomCreate) btnHeaderRoomCreate.style.display = "none";
     return;
    }

    const data = await res.json();
    const name = data.name || "";
    const email = data.email || "";
    const handle =
      email && email.includes("@")
        ? email.split("@")[0]
        : name || "user";

    if (headerUsername) headerUsername.textContent = handle;

    authButtons && (authButtons.style.display = "none");
    profileArea && (profileArea.style.display = "flex");
    if (btnHeaderRoomCreate) btnHeaderRoomCreate.style.display = "inline-flex";
  } catch (e) {
    if (e.message === "AUTH") {
        // 토큰 이상 → 비로그인 처리
        clearTokens();
        authButtons && (authButtons.style.display = "flex");
        profileArea && (profileArea.style.display = "none");
        if (btnHeaderRoomCreate) btnHeaderRoomCreate.style.display = "none";
    }else {
        console.warn("header init error:", e);
    }
  }

  if (btnLogout) {
    btnLogout.addEventListener("click", async () => {
      const ok = confirm("로그아웃 하시겠습니까?");
      if (!ok) return;
      try {
        await apiRequest("/api/auth/logout", { method: "POST" });
      } catch (e) {
        console.error(e);
      } finally {
        clearTokens();
        location.reload();
      }
    });
  }
});

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
    authButtons && (authButtons.style.display = "flex");
    profileArea && (profileArea.style.display = "none");
    return;
  }

  try {
    const res = await apiRequest("/api/members/me", { method: "GET" });
    if (!res.ok) throw new Error();

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
  } catch (e) {
    // 토큰 이상 → 비로그인 처리
    clearTokens();
    authButtons && (authButtons.style.display = "flex");
    profileArea && (profileArea.style.display = "none");
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

// me-test.js
import {
  getAccessToken,
  getRefreshToken,
  saveTokens,
  clearTokens,
} from "./authTokenStorage.js";
import { apiRequest } from "./apiClient.js";

function renderTokens() {
  document.querySelector("#accessTokenBox").textContent =
    getAccessToken() || "(없음)";
  document.querySelector("#refreshTokenBox").textContent =
    getRefreshToken() || "(없음)";
}

// /api/members/me 호출
async function handleLoadMe() {
  const res = await apiRequest("/api/members/me", { method: "GET" });

  const text = await res.text(); // JSON 이지만 일단 보기 쉽게 text로
  document.querySelector("#meResult").textContent = text;

  renderTokens();
}

// Access Token 일부러 망가뜨리기 (자동 /refresh 테스트용)
function handleBreakAccessToken() {
  const token = getAccessToken();
  if (!token) {
    alert("Access Token이 없습니다. 먼저 로그인 해주세요.");
    return;
  }

  const broken = token.substring(0, 10); // 앞 10글자만 남겨서 망가뜨림
  saveTokens({
    accessToken: broken,
    refreshToken: getRefreshToken(),
    tokenType: "Bearer",
  });

  alert("Access Token을 일부러 망가뜨렸습니다. 이제 /me 호출 시 자동 /refresh 되는지 확인하세요.");
  renderTokens();
}

// 로그아웃 (서버 + 클라이언트 토큰 삭제)
async function handleLogout() {
  try {
    await apiRequest("/api/auth/logout", { method: "POST" });
  } catch (e) {
    console.warn("서버 로그아웃 실패, 클라이언트 토큰만 삭제:", e);
  } finally {
    clearTokens();
    renderTokens();
    document.querySelector("#meResult").textContent = "";
    alert("로그아웃 완료 (토큰 삭제)");
  }
}

document.querySelector("#loadMeBtn").addEventListener("click", handleLoadMe);
document
  .querySelector("#breakAccessBtn")
  .addEventListener("click", handleBreakAccessToken);
document.querySelector("#logoutBtn").addEventListener("click", handleLogout);

document.addEventListener("DOMContentLoaded", renderTokens);

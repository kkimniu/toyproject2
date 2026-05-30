// login-test.js
import { saveTokens, getAccessToken, getRefreshToken } from "../common/authTokenStorage.js";

function renderTokens() {
  document.querySelector("#accessTokenBox").textContent =
    getAccessToken() || "(없음)";
  document.querySelector("#refreshTokenBox").textContent =
    getRefreshToken() || "(없음)";
}

async function handleLogin(event) {
  event.preventDefault();

  const email = document.querySelector("#email").value;
  const password = document.querySelector("#password").value;

  const res = await fetch("/api/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });

  if (!res.ok) {
    alert("로그인 실패: " + res.status);
    return;
  }

  const data = await res.json();

  // 서버 응답 필드명(snake_case)에 맞게
  saveTokens({
    accessToken: data.access_token,
    refreshToken: data.refresh_token,
    tokenType: data.token_type,
  });

  alert("로그인 성공!");
  renderTokens();
}

document
  .querySelector("#loginForm")
  .addEventListener("submit", handleLogin);

document.addEventListener("DOMContentLoaded", renderTokens);

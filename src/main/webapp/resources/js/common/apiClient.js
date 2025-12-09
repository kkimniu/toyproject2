// apiClient.js
// AccessToken 자동 첨부 + 401 시 RefreshToken으로 재발급 + 재요청

import {
  getAccessToken,
  getRefreshToken,
  getTokenType,
  saveTokens,
  clearTokens,
} from "./authTokenStorage.js";

// 비동기 딜레이(디버깅용으로 쓰고 싶으면 유지, 아니면 삭제 가능)
function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

// 공통 API 요청 함수
export async function apiRequest(input, init = {}) {
  // 1) 기존 옵션 복사
  const options = { ...init };

  const isFormData = options.body instanceof FormData;

  options.headers = {
    ...(init.headers || {}),
  };

  if(!isFormData) {
    options.headers["Content-Type"] =
      init.headers && init.headers["Content-Type"]
        ? init.headers["Content-Type"]
        : "application/json";
  }


  const accessToken = getAccessToken();
  if (accessToken) {
    options.headers["Authorization"] = `${getTokenType()} ${accessToken}`;
  }

  // 1차 요청
  let response = await fetch(input, options);

  // 401 아니면 그대로 반환
  if (response.status !== 401) {
    return response;
  }

  // 401 → AccessToken 만료 가능성 → Refresh 시도
  const refreshToken = getRefreshToken();
  if (!refreshToken) {
    // RefreshToken도 없으면 그냥 로그아웃 처리
    clearTokens();
    alert("로그인이 만료되었습니다. 다시 로그인 해주세요.");

    if(typeof window.openAuthModal == "function") {
        window.openAuthModal("login");
    }
    // 필요하면 location.href = "/login"; 이런 거 추가
    return response;
  }



  // 2) Refresh 요청
  const refreshResponse = await fetch("/api/auth/refresh", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      refresh_token: refreshToken,
    }),
  });

  if (!refreshResponse.ok) {
    // refresh 실패 → 토큰 삭제 후 로그인 페이지로 유도
    clearTokens();
    alert("세션이 만료되었습니다. 다시 로그인 해주세요.");
    if (typeof window.openAuthModal === "function") {
      window.openAuthModal("login");
    }
    return response;
  }

  const refreshData = await refreshResponse.json();

  // 응답 필드 이름은 서버 snake_case 기준
  saveTokens({
    accessToken: refreshData.access_token,
    refreshToken: refreshData.refresh_token,
    tokenType: refreshData.token_type,
  });

  // 🔁 새 AccessToken으로 원래 요청 다시 한번 시도
  const retryOptions = { ...options };
  retryOptions.headers = {
    ...(options.headers || {}),
    Authorization: `${refreshData.token_type} ${refreshData.access_token}`,
  };

  const retryResponse = await fetch(input, retryOptions);
  return retryResponse;
}

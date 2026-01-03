// apiClient.js
import {
  getAccessToken,
  getTokenType,
  saveTokens,
  clearTokens,
} from "./authTokenStorage.js";

// 공통 API 요청 함수
export async function apiRequest(input, init = {}) {
  // 1) 기존 옵션 복사
  const options = { ...init };
  options._retried = options._retried ?? false;

  const isFormData = options.body instanceof FormData;

  options.headers = {
    ...(init.headers || {}),
  };

  if(!isFormData) {
      options.headers["Content-Type"] = init.headers && init.headers["Content-Type"] ? init.headers["Content-Type"] : "application/json";
  }

  // (중요) 쿠키 기반 refresh 사용 시
  // - 같은 도메인이면 없어도 쿠키가 가지만,
  // - React(다른 포트/도메인)까지 고려하면 include를 강제하는 게 안전
  options.credentials = options.credentials ?? "include";

  // AccessToken 있으면 Authorization 붙이기
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

  // ===== AccessToken 만료 → Refresh 시도 =====
  const refreshResponse = await fetch("/api/auth/refresh", {
      method: "POST",
      credentials: "include", // HttpOnly refresh 쿠키 자동 전송
  });

  if (!refreshResponse.ok) {
     // refresh 실패 → 로그아웃 처리
    clearTokens();
    alert("세션이 만료되었습니다. 다시 로그인 해주세요.");
    if(typeof window.openAuthModal == "function") window.openAuthModal("login");
    return response;
  }

  const refreshData = await refreshResponse.json();

  // 응답 필드 이름은 서버 snake_case 기준
  saveTokens({ accessToken: refreshData.access_token, tokenType: refreshData.token_type });

  // 🔁 새 AccessToken으로 원래 요청 다시 한번 시도
  const retryOptions = { ...options, _retried: true  };
  retryOptions.headers = {
    ...(options.headers || {}),
    Authorization: `${refreshData.token_type} ${refreshData.access_token}`,
  };

  const retryResponse = await fetch(input, retryOptions);
  return retryResponse;
}

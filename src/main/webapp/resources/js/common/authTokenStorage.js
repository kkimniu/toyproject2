// authTokenStorage.js
// - 토큰 저장/조회/삭제를 한 곳에서 관리하기 위한 유틸리티
// - 나중에 localStorage -> cookie로 바꾸고 싶어도 이 파일만 바꾸면 되도록 분리함

const ACCESS_TOKEN_KEY = "accessToken";
const REFRESH_TOKEN_KEY = "refreshToken";
const TOKEN_TYPE_KEY = "tokenType";

// 로그인 성공 시 토큰을 저장
export function saveTokens({ accessToken, refreshToken, tokenType }) {
  localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
  localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
  localStorage.setItem(TOKEN_TYPE_KEY, tokenType || "Bearer");
}

// 저장된 토큰 가져오기
export function getAccessToken() {
  return localStorage.getItem(ACCESS_TOKEN_KEY);
}

export function getRefreshToken() {
  return localStorage.getItem(REFRESH_TOKEN_KEY);
}

export function getTokenType() {
  return localStorage.getItem(TOKEN_TYPE_KEY) || "Bearer";
}

// 로그아웃 시 토큰 제거
export function clearTokens() {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
  localStorage.removeItem(TOKEN_TYPE_KEY);
}

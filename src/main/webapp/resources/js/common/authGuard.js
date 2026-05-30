// /resources/js/common/authGuard.js
// 로그인 필요 기능에서 공통으로 쓰는 유틸

import { getAccessToken } from "../common/authTokenStorage.js";

/**
 * 로그인 여부 체크
 * - 토큰 없으면 로그인 모달(#authModal) 띄우고 false 반환
 * - 토큰 있으면 true 반환
 */
export function requireLogin() {
  const token = getAccessToken();

  // 비로그인 상태
  if (!token) {
    alert("로그인이 필요한 서비스입니다.");
    // login.js 에서 전역으로 등록해둔 openAuthModal 사용
    if (typeof window.openAuthModal === "function") {
      window.openAuthModal("login"); // 기본 탭을 로그인으로
    } else {
      // 혹시 모달 스크립트가 아직 안 올라온 경우 대비
       console.warn("openAuthModal 이 아직 준비가 안 됐어요.");
    }
    return false;
  }

  // 로그인 상태
  return true;
}

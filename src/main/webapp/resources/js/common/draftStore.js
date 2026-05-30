// /resources/js/common/draftStore.js
import { getAccessToken } from "./authTokenStorage.js";

/**
 * 프론트 안전 Draft Store
 * - 페이지/기능별 격리
 * - 사용자별 격리(로그인/게스트)
 * - meta 검증 + TTL로 오염/충돌 방지
 */

function getOrCreateGuestScope() {
  // 게스트도 탭/브라우저마다 분리되도록 랜덤 id 저장
  const k = "guest_scope_id";
  let id = sessionStorage.getItem(k);
  if (!id) {
    id = `${Date.now()}_${Math.random().toString(16).slice(2)}`;
    sessionStorage.setItem(k, id);
  }
  return `guest:${id}`;
}

export function makeUserScope() {
  const token = getAccessToken();
  if (token) return `user:${token.slice(0, 12)}`; // 토큰 prefix로 사용자 스코프 분리
  return getOrCreateGuestScope();
}

export function makeDraftKey({ app = "myapp", page, userScope, version = "v1" }) {
  // key 자체는 상수로 고정하지 않고 "조합"으로 만든다
  return `draft:${app}:${page}:${userScope}:${version}`;
}

export function saveDraft({ key, page, userScope, version = "v1", ttlMs = 1000 * 60 * 60 * 24, data }) {
  const payload = {
    meta: {
      page,
      user_scope: userScope,
      schema_version: version,
      saved_at: Date.now(),
      ttl_ms: ttlMs,
      path: location.pathname,
    },
    data,
  };
  localStorage.setItem(key, JSON.stringify(payload));
}

export function loadDraft({ key, page, userScope, version = "v1" }) {
  try {
    const raw = localStorage.getItem(key);
    if (!raw) return null;

    const parsed = JSON.parse(raw);
    if (!parsed?.meta || !parsed?.data) return null;

    const m = parsed.meta;

    // 페이지/유저/버전 검증
    if (m.page !== page) return null;
    if (m.user_scope !== userScope) return null;
    if (m.schema_version !== version) return null;

    // path 검증 (다른 화면에서 저장된 거면 버림)
    if (m.path !== location.pathname) return null;

    // TTL 검증
    const ttl = Number(m.ttl_ms ?? 0);
    const savedAt = Number(m.saved_at ?? 0);
    if (!ttl || !savedAt) return null;
    if (Date.now() - savedAt > ttl) return null;

    return parsed.data;
  } catch {
    return null;
  }
}

export function clearDraft(key) {
  localStorage.removeItem(key);
}

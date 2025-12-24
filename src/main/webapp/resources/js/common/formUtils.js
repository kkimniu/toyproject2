// formUtils.js
// 공용 폼 입력 유틸

// value: 문자열(trim)
export function v(id) {
  return (document.getElementById(id)?.value ?? "").trim();
}

// number: 숫자 or null
export function n(id) {
  const s = v(id);
  if (s === "") return null;
  const num = Number(s);
  return Number.isFinite(num) ? num : null;
}

// integer: 정수 or null
export function i(id) {
  const s = v(id);
  if (s === "") return null;
  const num = Number(s);
  return Number.isFinite(num) ? Math.trunc(num) : null;
}

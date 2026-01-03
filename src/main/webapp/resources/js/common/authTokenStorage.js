// authTokenStorage.js

const ACCESS_TOKEN_KEY = "access_token";
const TOKEN_TYPE_KEY = "token_type"; // 보통 "Bearer"

export function saveTokens({ accessToken, tokenType }) {
    if (accessToken) localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
    if (tokenType) localStorage.setItem(TOKEN_TYPE_KEY, tokenType);
}

export function getAccessToken() {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
}

export function getTokenType() {
    return localStorage.getItem(TOKEN_TYPE_KEY) || "Bearer";
}

export function clearTokens() {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(TOKEN_TYPE_KEY);
}
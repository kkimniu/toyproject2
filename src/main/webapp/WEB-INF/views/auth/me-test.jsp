<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>내 정보 + 토큰 자동 재발급 테스트</title>
</head>
<body>
<h1>내 정보 조회 테스트</h1>

<button id="loadMeBtn">/api/members/me 호출</button>
<button id="breakAccessBtn">Access Token 망가뜨리기</button>
<button id="logoutBtn">로그아웃</button>

<hr>

<h3>내 정보 응답 JSON</h3>
<pre id="meResult"></pre>

<hr>

<h3>현재 토큰 상태</h3>
<p>Access Token:</p>
<pre id="accessTokenBox"></pre>

<p>Refresh Token:</p>
<pre id="refreshTokenBox"></pre>

<script type="module" src="/resources/js/auth/mypage.js"></script>
</body>
</html>
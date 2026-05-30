<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>로그인 테스트</title>
</head>
<body>
<h1>로그인 테스트 페이지</h1>

<form id="loginForm">
    <div>
        <label>Email: </label>
        <input type="text" id="email" value="test@naver.com">
    </div>
    <div>
        <label>Password: </label>
        <input type="password" id="password" value="12345678">
    </div>
    <button type="submit">로그인</button>
</form>

<hr>

<p>현재 저장된 Access Token:</p>
<pre id="accessTokenBox"></pre>

<p>현재 저장된 Refresh Token:</p>
<pre id="refreshTokenBox"></pre>

<script type="module" src="/resources/js/auth/login-test.js"></script>
</body>
</html>

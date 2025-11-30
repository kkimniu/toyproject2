<%@ page contentType="text/html;charset=UTF-8" %>
<div class="header">
  <div class="logo">Roommate</div>

  <div class="header-right">
    <button id="btnOpenLogin" type="button">로그인</button>
    <button id="btnOpenRegister" type="button">회원가입</button>
    <button id="btnLogout" type="button">로그아웃</button>
  </div>
</div>

<jsp:include page="/WEB-INF/views/auth/login.jsp"/>
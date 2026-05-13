<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/resources/css/common/header.css" />

<div class="header">
  <div class="header-left">
    <a href="${pageContext.request.contextPath}/main" class="logo">RoomMate</a>

    <ul class="gnb">
      <li><a href="${pageContext.request.contextPath}/main">홈</a></li>
      <li><a href="${pageContext.request.contextPath}/rooms">룸메이트 찾기</a></li>
      <li><a href="${pageContext.request.contextPath}/rooms/map">지도 검색</a></li>
      <li><a href="${pageContext.request.contextPath}/chat/rooms">채팅</a></li>
      <li><a href="${pageContext.request.contextPath}/community">커뮤니티</a></li>
      <li><a href="${pageContext.request.contextPath}/notices">공지사항</a></li>
    </ul>
  </div>

  <div class="header-right">
    <a href="${pageContext.request.contextPath}/rooms/roomCreate"
       id="btnHeaderRoomCreate"
       class="header-primary-btn"
       style="display:none;">
      방 등록
    </a>

    <div id="headerAuthButtons">
      <button id="btnOpenLogin" type="button" class="header-text-btn">로그인</button>
      <button id="btnOpenRegister" type="button" class="header-text-btn header-join-btn">회원가입</button>
    </div>

    <div id="headerProfileArea" class="header-profile" style="display:none;">
      <a href="${pageContext.request.contextPath}/members/mypage"
         class="header-username"
         id="headerUsername">user</a>
      <button id="btnLogout" type="button" class="header-text-btn">로그아웃</button>
    </div>
  </div>
</div>

<script type="module" src="${pageContext.request.contextPath}/resources/js/common/header.js"></script>

<jsp:include page="/WEB-INF/views/auth/login.jsp"/>

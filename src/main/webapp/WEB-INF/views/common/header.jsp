<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/resources/css/common/header.css" />

<div class="header">
  <!-- 왼쪽: 로고 + GNB -->
  <div class="header-left">
    <a href="${pageContext.request.contextPath}/" class="logo">룸메이트</a>

    <ul class="gnb">
      <li><a href="${pageContext.request.contextPath}/">홈</a></li>
      <li><a href="${pageContext.request.contextPath}/rooms/map">지도 검색</a></li>
      <li><a href="${pageContext.request.contextPath}/rooms">룸메이트 찾기</a></li>
      <li><a href="${pageContext.request.contextPath}/community">커뮤니티</a></li>
      <li><a href="${pageContext.request.contextPath}/notices">공지사항</a></li>
    </ul>
  </div>

  <!-- 오른쪽: 검색 / 알림 / 로그인/프로필 -->
  <div class="header-right">

   <!-- ✅ 방 등록 버튼 (로그인일 때만 노출 권장) -->
   <a href="${pageContext.request.contextPath}/rooms/roomCreate"
      id="btnHeaderRoomCreate"
      class="header-primary-btn"
      style="display:none;">
     + 방 등록
   </a>

    <!-- 검색 아이콘 -->
    <button type="button" class="header-icon-btn" id="btnHeaderSearch">
      🔍
    </button>

    <!-- 알림 아이콘 + 뱃지 (나중에 알림 API 붙이면 숫자 갱신) -->
    <button type="button" class="header-icon-btn" id="btnHeaderNotification">
      🔔
      <span class="badge" id="notificationCount" style="display:none;">0</span>
    </button>

    <!-- 로그인/회원가입 버튼: 비로그인일 때만 노출 -->
    <div id="headerAuthButtons">
      <button id="btnOpenLogin" type="button" class="header-text-btn">로그인</button>
      <button id="btnOpenRegister" type="button" class="header-text-btn">회원가입</button>
    </div>

    <!-- 프로필 + 로그아웃: 로그인일 때만 노출 -->
    <div id="headerProfileArea" class="header-profile" style="display:none;">
      <span class="icon-user">👤</span>
      <a href="${pageContext.request.contextPath}/members/mypage"
         class="header-username"
         id="headerUsername">user</a>
      <button id="btnLogout" type="button" class="header-text-btn">로그아웃</button>
    </div>
  </div>
</div>

<script type="module" src="${pageContext.request.contextPath}/resources/js/common/header.js"></script>

<!-- 로그인/회원가입 모달 (login.jsp가 모달 전용이라면 그대로 include) -->
<jsp:include page="/WEB-INF/views/auth/login.jsp"/>

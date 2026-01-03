<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>상대방페이지</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/member/profile-view.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<main class="mypage-main" id="profilePage" data-member-id="${memberId}">
  <!--  상단 프로필 요약 카드 (스샷처럼 가로로 긴 카드) -->
  <section class="profile-summary-card">
    <div class="profile-summary-top">
      <!-- 왼쪽: 프로필 사진 -->
      <div class="profile-summary-photo">
        <img id="profilePhoto" src="${pageContext.request.contextPath}/resources/img/default-profile.png" alt="프로필 사진" class="profile-photo-lg">
      </div>

      <!-- 가운데: 이름 / 이메일 / 전화번호 / 가입일 -->
      <div class="profile-summary-info">
        <p class="profile-label" id="profileLabel">사용자 프로필</p>
        <h2 id="profileName" class="profile-name">이름</h2>
        <div class="profile-contact">
          <span id="profileJoinedAt" class="profile-contact-item">가입일: 2024. 1. 1.</span>
        </div>
      </div>

      <!-- 오른쪽: 문의하기 버튼 -->
      <div class="profile-summary-actions">

      </div>
    </div>

    <!--  하단: 태그 + 취미/선호/반려동물 (네가 준 내용 그대로 포함) -->
    <div class="profile-summary-bottom">

      <!-- 첫 줄: 라이프스타일 태그 -->
      <div class="profile-tags-row">
        <span class="tag-pill" id="profileWorkType">직업/라이프스타일</span>
        <span class="tag-pill" id="profileMbti">MBTI</span>
        <span class="tag-pill" id="profileSmoking">흡연</span>
        <span class="tag-pill" id="profileDrinking">음주</span>
        <span class="tag-pill" id="profileSleepTime">수면 시간</span>
      </div>

      <!-- 둘째 줄: 취미 / 생활선호 / 반려동물 -->
      <div class="profile-detail-row">

        <div class="profile-subsection">
          <h3>취미</h3>
          <div id="profileHobbies" class="chip-list"></div>
        </div>

        <div class="profile-subsection">
          <h3>생활 선호</h3>
          <div id="profilePreferences" class="chip-list"></div>
        </div>

        <div class="profile-subsection">
          <h3>반려동물</h3>
          <div id="profilePets" class="chip-list"></div>
        </div>

      </div>
    </div>
  </section>

  <!--  아래쪽: 탭 영역 (등록한 게시글 / 활동 내역 ) -->
  <nav class="mypage-tabs">
    <button class="mypage-tab" data-tab="posts">등록한 게시글</button>
    <button class="mypage-tab" data-tab="activities">활동 내역</button>
  </nav>

  <section class="mypage-tab-content" id="tab-posts" style="display:none;">
      <div class="mypage-posts-head">
        <h3 class="mypage-posts-title">등록한 게시글</h3>
      </div>

      <div id="userRoomList" class="my-room-list">
        <p class="favorite-empty">등록한 방이 없습니다.</p>
      </div>
  </section>

  <section class="mypage-tab-content" id="tab-activities" style="display:none;">
    <!-- TODO: 활동 내역 내용 -->
  </section>
</main>

<script type="module" src="${pageContext.request.contextPath}/resources/js/member/profileView.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>
</body>
</html>
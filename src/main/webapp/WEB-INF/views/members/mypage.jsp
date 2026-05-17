<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>마이페이지</title>
    <link rel="stylesheet" href="/resources/css/member/mypage-view.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<main class="mypage-main">
  <!-- ✅ 상단 프로필 요약 카드 (스샷처럼 가로로 긴 카드) -->
  <section class="profile-summary-card">
    <div class="profile-summary-top">
      <!-- 왼쪽: 프로필 사진 + 사진 변경 버튼 -->
      <div class="profile-summary-photo">
        <img id="profilePhoto"
             src="/resources/img/default-profile.svg"
             alt="프로필 사진"
             class="profile-photo-lg">
        <input type="file" id="photoFileInput" accept="image/*" style="display:none;">
        <button type="button"
                class="btn-photo-upload"
                onclick="document.getElementById('photoFileInput').click();">
          사진 변경
        </button>
      </div>

      <!-- 가운데: 이름 / 이메일 / 전화번호 / 가입일 -->
      <div class="profile-summary-info">
        <p class="profile-label">내 프로필</p>
        <h2 id="profileName" class="profile-name">이름</h2>
        <!-- 필요하면 핸들용 span 하나 더 둘 수도 있음 -->
        <!-- <p id="profileHandle" class="profile-handle">@username</p> -->

        <div class="profile-contact">
          <span id="profileEmail" class="profile-contact-item">email@example.com</span>
          <span id="profilePhone" class="profile-contact-item">010-0000-0000</span>
          <span id="profileJoinedAt" class="profile-contact-item">가입일: 2024. 1. 1.</span>
        </div>
      </div>

      <!-- 오른쪽: 프로필 수정 버튼 -->
      <div class="profile-summary-actions">
        <button type="button"
                class="btn btn-dark"
                onclick="location.href='${pageContext.request.contextPath}/members/mypage/edit'">
          프로필 수정
        </button>
      </div>
    </div>

    <!-- ✅ 하단: 태그 + 취미/선호/반려동물 (네가 준 내용 그대로 포함) -->
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

  <!-- ✅ 아래쪽: 탭 영역 (관심 목록 / 내 게시글 / 활동 내역 / 계정 설정 등) -->
  <nav class="mypage-tabs">
    <button class="mypage-tab active" data-tab="favorites">관심 목록</button>
    <button class="mypage-tab" data-tab="posts">내 게시글</button>
    <button class="mypage-tab" data-tab="activities">활동 내역</button>
    <button class="mypage-tab" data-tab="account">계정 설정</button>
  </nav>

  <section class="mypage-tab-content" id="tab-favorites">
  <div class="mypage-section-header">
    <h2 class="mypage-section-title">관심 방</h2>
    <p class="mypage-section-subtitle">찜한 방들을 한눈에 볼 수 있어요.</p>
  </div>

  <div id="favoriteList" class="favorite-list">
    <!-- JS에서 카드로 렌더링 -->
    <p class="favorite-empty">아직 관심 등록한 방이 없습니다.</p>
  </div>
 </section>

  <section class="mypage-tab-content" id="tab-posts" style="display:none;">
      <div class="mypage-posts-head">
        <h3 class="mypage-posts-title">내 게시글</h3>

        <a class="btn-post-create"
           href="${pageContext.request.contextPath}/rooms/roomCreate">
          + 새 게시글 작성
        </a>
      </div>

      <div id="myRoomList" class="my-room-list">
        <p class="favorite-empty">내가 등록한 방이 없습니다.</p>
      </div>
  </section>

  <section class="mypage-tab-content" id="tab-activities" style="display:none;">
    <div class="mypage-section-header">
      <h2 class="mypage-section-title">신고 내역</h2>
      <p class="mypage-section-subtitle">내가 접수한 신고와 처리 결과를 확인할 수 있습니다.</p>
    </div>

    <div id="myReportList" class="my-report-list">
      <p class="favorite-empty">신고 내역을 불러오는 중입니다.</p>
    </div>
  </section>

  <!-- 🔐 계정 설정 탭 안에 비밀번호 변경 카드 + 모달 배치하면 됨 -->
  <section class="mypage-tab-content" id="tab-account" style="display:none;">
    <div class="mypage-section mypage-section--security">
      <h3>비밀번호 변경</h3>
      <p>비밀번호를 정기적으로 변경해 계정 보안을 강화하세요.</p>
      <button type="button"
              id="btnOpenPasswordModal"
              class="btn btn-primary">
        비밀번호 변경
      </button>
    </div>
  </section>
  <!-- 🔐 비밀번호 변경 모달 -->
  <div id="passwordModalOverlay" class="password-modal-overlay" style="display:none;">
    <div class="password-modal">
      <h3>비밀번호 변경</h3>

      <div class="password-modal-body">
        <div class="form-group">
          <label for="currentPassword">현재 비밀번호</label>
          <input type="password" id="currentPassword" class="form-control">
        </div>

        <div class="form-group">
          <label for="newPassword">새 비밀번호</label>
          <input type="password" id="newPassword" class="form-control">
        </div>

        <div class="form-group">
          <label for="confirmPassword">새 비밀번호 확인</label>
          <input type="password" id="confirmPassword" class="form-control">
        </div>

        <p id="passwordChangeMessage" class="password-message"></p>
      </div>

      <div class="password-modal-footer">
        <button type="button" id="btnCancelPasswordModal" class="btn btn-secondary">
          취소
        </button>
        <button type="button" id="btnChangePassword" class="btn btn-primary">
          비밀번호 변경
        </button>
      </div>
    </div>
  </div>
</main>

<script type="module" src="${pageContext.request.contextPath}/resources/js/member/mypageView.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>
</body>
</html>

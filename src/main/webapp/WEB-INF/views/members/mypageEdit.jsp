<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>마이페이지 - Roommate</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/member/mypage-edit.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="mypage-container">
  <!-- 왼쪽: 프로필 카드 -->
  <section class="mypage-left">
    <div class="profile-card">
      <div class="profile-photo-wrapper">
        <img id="profilePhoto" src="" alt="프로필 사진" class="profile-photo">
        <input type="file" id="photoFileInput" accept="image/*" style="display: none;"/>
        <button type="button" class="btn-photo-upload" onclick="document.getElementById('photoFileInput').click();">
        사진 변경
        </button>
      </div>
      <div class="profile-main-info">
        <h2 id="profileName">이름</h2>
        <p id="profileEmail" class="profile-email">email@example.com</p>
        <p id="profilePhone" class="profile-phone">010-0000-0000</p>
      </div>

      <div class="profile-tags">
        <span class="tag" id="profileWorkType">직업/라이프스타일</span>
        <span class="tag" id="profileMbti">MBTI</span>
        <span class="tag" id="profileSmoking">흡연</span>
        <span class="tag" id="profileDrinking">음주</span>
        <span class="tag" id="profileSleepTime">수면 시간</span>
      </div>

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
  </section>

  <!-- 오른쪽: 내 정보 수정 폼 -->
  <section class="mypage-right">
    <h2>내 프로필 수정</h2>

    <form id="mypageForm">
      <div class="form-row">
        <label for="mpName">이름</label>
        <input type="text" id="mpName" maxlength="50">
      </div>

      <div class="form-row">
        <label for="mpPhone">전화번호</label>
        <input type="text" id="mpPhone" placeholder="010-1234-5678">
      </div>

      <div class="form-row">
        <label for="mpWorkType">직업/라이프스타일</label>
        <select id="mpWorkType">
          <option value="">선택해주세요</option>
          <!-- /api/members/form-codes로 채움 -->
        </select>
      </div>

      <div class="form-row">
        <label for="mpSleepTime">취침 시간</label>
        <select id="mpSleepTime">
          <option value="">선택 안 함</option>
          <option value="EARLY">일찍 잠 (22시 이전)</option>
          <option value="NORMAL">보통 (22~24시)</option>
          <option value="LATE">늦게 잠 (자정 이후)</option>
        </select>
      </div>

      <div class="form-row">
        <label for="mpSmoking">흡연 여부</label>
        <select id="mpSmoking">
          <option value="NON_SMOKER">비흡연</option>
          <option value="SMOKER">흡연</option>
        </select>
      </div>

      <div class="form-row">
        <label for="mpDrinking">음주 빈도</label>
        <select id="mpDrinking">
          <option value="NONE">안 마심</option>
          <option value="SOCIAL">가끔</option>
          <option value="OFTEN">자주</option>
        </select>
      </div>

      <div class="form-row">
        <label for="mpMbti">MBTI</label>
        <select id="mpMbti">
            <option value="">선택 안함</option>
            <option value="INTJ">INTJ</option>
            <option value="INTP">INTP</option>
            <option value="INFJ">INFJ</option>
            <option value="INFP">INFP</option>
            <option value="ISTJ">ISTJ</option>
            <option value="ISTP">ISTP</option>
            <option value="ISFJ">ISFJ</option>
            <option value="ISFP">ISFP</option>
            <option value="ENTJ">ENTJ</option>
            <option value="ENTP">ENTP</option>
            <option value="ENFJ">ENFJ</option>
            <option value="ENFP">ENFP</option>
            <option value="ESTJ">ESTJ</option>
            <option value="ESTP">ESTP</option>
            <option value="ESFJ">ESFJ</option>
            <option value="ESFP">ESFP</option>
        </select>
      </div>

      <div class="form-row">
        <label>취미</label>
        <div id="mpHobbyCheckboxList" class="checkbox-group">
          <!-- form-codes로 렌더링 -->
        </div>
      </div>

      <div class="form-row">
        <label>생활 선호</label>
        <div id="mpPreferenceCheckboxList" class="checkbox-group">
          <!-- form-codes로 렌더링 -->
        </div>
      </div>

      <div class="form-row">
        <label>반려동물</label>
        <div id="mpPetCheckboxList" class="checkbox-group">
          <!-- form-codes로 렌더링 -->
        </div>
      </div>

      <div class="form-actions">
        <span id="mypageError" class="form-error"></span>
        <button type="submit" class="btn-primary">저장하기</button>
      </div>
    </form>
  </section>
</div>

<script type="module" src="${pageContext.request.contextPath}/resources/js/member/mypageEdit.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>
</body>
</html>
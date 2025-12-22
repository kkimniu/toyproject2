<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Roommate</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/login.css">
</head>
<body>

<div id="authModal" class="auth-modal hidden">
  <div class="auth-modal-backdrop" onclick="closeAuthModal()"></div>

  <div class="auth-modal-box">
    <div class="auth-tabs">
      <button class="auth-tab active" data-tab="login">로그인</button>
      <button class="auth-tab" data-tab="register">회원가입</button>
    </div>

    <!-- 로그인 -->
    <div class="auth-tab-content" id="loginTab">
      <form id="loginForm">
        <input type="email" id="loginEmail" placeholder="이메일" required />
        <input type="password" id="loginPassword" placeholder="비밀번호" required />
        <button type="submit">로그인</button>
        <p id="loginError" class="error"></p>
      </form>
    </div>

    <!-- 회원가입 탭 -->
    <div id="registerTab" class="auth-tab-content hidden">
      <form id="registerForm">

        <div class="register-profile-photo-wrapper">
          <img id="regProfilePhoto" src="" alt="프로필 사진" class="register-profile-photo">
          <input type="file" id="regPhotoFileInput" accept="image/*" style="display: none;"/>
          <input type="hidden" id="regProfileTempFileId" />
          <input type="hidden" id="regSignupKey" />
          <button type="button" id="btnRegPhotoUpload" class="btn-regPhoto-upload">
            사진 추가
          </button>
        </div>

        <!-- 1. 기본 정보: 이름 -->
        <div class="form-row">
          <div class="form-group">
            <label for="regName">이름</label>
            <input type="text" id="regName" placeholder="실명을 입력하세요" required>
          </div>
        </div>

        <!-- 2. 이메일 -->
        <div class="form-group">
          <label for="regEmail">이메일</label>
          <input type="email" id="regEmail" placeholder="example@email.com" required>
        </div>

        <!-- 3. 전화번호 -->
        <div class="form-group">
          <label for="regPhone">전화번호</label>
          <input type="text" id="regPhone" placeholder="010-1234-5678" required>
        </div>

        <!-- 4. 비밀번호 / 비밀번호 확인 -->
        <div class="form-row">
          <div class="form-group">
            <label for="regPassword">비밀번호</label>
            <input type="password" id="regPassword" placeholder="비밀번호" required>
          </div>

          <div class="form-group">
            <label for="regConfirm">비밀번호 확인</label>
            <input type="password" id="regConfirm" placeholder="비밀번호 확인" required>
          </div>
        </div>

        <!-- 5. 흡연 여부 -->
        <div class="form-group">
          <label for="regSmoking">흡연 여부</label>
          <select id="regSmoking" required>
            <option value="NON_SMOKER">비흡연</option>
            <option value="SMOKER">흡연</option>
          </select>
        </div>

        <!-- 6. 음주 여부 -->
        <div class="form-group">
          <label for="regDrinking">음주 여부</label>
          <select id="regDrinking" required>
            <option value="NONE">안 함</option>
            <option value="SOCIAL">가끔</option>
            <option value="OFTEN">자주</option>
          </select>
        </div>

        <!-- 7. 취침 시간 -->
        <div class="form-group">
          <label for="regSleepTime">취침 시간</label>
          <select id="regSleepTime">
            <option value="">선택 안 함</option>
            <option value="EARLY">일찍 잠 (22시 이전)</option>
            <option value="NORMAL">보통 (22~24시)</option>
            <option value="LATE">늦게 잠 (자정 이후)</option>
          </select>
        </div>

        <!-- 8. 직업/생활 타입 (work_type_id) -->
        <div class="form-group">
          <label for="regWorkType">직업 / 라이프스타일</label>
          <select id="regWorkType" required>
            <!-- JS에서 /api/members/work-types or /form-codes로 옵션 채움 -->
          </select>
        </div>

        <!-- 9. MBTI -->
        <div class="form-group">
          <label for="regMbti">MBTI</label>
          <select id="regMbti">
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

        <!-- 11. 취미 다중 선택 -->
        <div class="form-group">
          <label>취미</label>
          <div id="hobbyCheckboxList" class="checkbox-grid">
            <!-- JS에서 /api/members/hobbies or form-codes로 채움 -->
          </div>
        </div>

        <!-- 12. 생활 선호(성향) 다중 선택 -->
        <div class="form-group">
          <label>생활 선호</label>
          <div id="preferenceCheckboxList" class="checkbox-grid">
            <!-- JS에서 /api/members/preferences or form-codes로 채움 -->
          </div>
        </div>

        <!-- 13. 반려동물 다중 선택 -->
        <div class="form-group">
          <label>반려동물</label>
          <div id="petCheckboxList" class="checkbox-grid">
            <!-- JS에서 /api/members/pets or form-codes로 채움 -->
          </div>
        </div>

        <p id="registerError" class="form-error"></p>

        <button type="submit" class="btn-primary w-100">
          회원가입
        </button>
      </form>
    </div>
  </div> <!-- /auth-modal-box -->
</div>   <!-- /authModal -->
</body>
</html>
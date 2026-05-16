<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>RoomMate | 메인</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/home/main.css">
</head>
<body>
  <jsp:include page="/WEB-INF/views/common/header.jsp"/>

  <main class="home-page">
    <section class="hero-section">
      <div class="hero-copy">
        <h1>내 조건에 맞는 룸메이트를<br>쉽게 찾아보세요</h1>
        <p class="hero-description">
          실제 등록된 방과 작성된 프로필을 기준으로 나와 잘 맞는 룸메이트를 빠르게 찾아보세요.
        </p>

        <form class="search-card" id="mainSearchForm">
          <label>
            <span>지역</span>
            <input type="text" name="region" placeholder="예: 강남구, 마포구">
          </label>
          <label>
            <span>예산</span>
            <select name="budget">
              <option value="">전체 예산</option>
              <option value="40">40만원 이하</option>
              <option value="60">60만원 이하</option>
              <option value="80">80만원 이하</option>
              <option value="100">100만원 이하</option>
            </select>
          </label>
          <label>
            <span>성별</span>
            <select name="gender">
              <option value="">전체</option>
              <option value="FEMALE">여성</option>
              <option value="MALE">남성</option>
              <option value="OTHER">기타</option>
            </select>
          </label>
          <button type="submit">검색</button>
        </form>
      </div>

      <div class="hero-media">
        <img
          src="https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=1200&q=80"
          alt="밝고 정돈된 거실 인테리어">
      </div>
    </section>

    <section class="recommend-section">
      <div class="section-heading">
        <h2>추천 룸메이트</h2>
      </div>

      <div class="recommend-layout">
        <aside class="filter-panel" aria-label="룸메이트 필터">
          <div id="dynamicFilterGroups">
            <div class="filter-empty">필터를 불러오는 중입니다.</div>
          </div>
          <button class="filter-submit" type="button" id="filterSubmit">필터 적용</button>
        </aside>

        <div class="roommate-grid" id="recommendedRoommateList">
          <div class="roommate-empty">추천 룸메이트를 불러오는 중입니다.</div>
        </div>
      </div>
    </section>
  </main>

  <script>
    window.contextPath = "${pageContext.request.contextPath}";
  </script>
  <script type="module" src="${pageContext.request.contextPath}/resources/js/home/main.js"></script>
  <script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>
</body>
</html>

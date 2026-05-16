<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>룸메이트 찾기 | RoomMate</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/room/room-list.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<main class="room-list-page">
    <section class="room-list-toolbar">
        <div>
            <h1>룸메이트 찾기</h1>
            <p>현재 모집 중인 방과 룸메이트 조건을 빠르게 확인하세요.</p>
        </div>
        <a class="toolbar-map-link" href="${pageContext.request.contextPath}/rooms/map">지도에서 보기</a>
    </section>

    <section class="room-search-panel">
        <form id="roomSearchForm" class="room-search-form">
            <label>
                <span>지역</span>
                <input type="search" name="region" placeholder="예: 마포, 신촌, 강남">
            </label>
            <label>
                <span>월세 상한</span>
                <input type="number" name="budget" min="0" step="1" placeholder="만원">
            </label>
            <label>
                <span>정렬</span>
                <select name="sort">
                    <option value="latest">최신순</option>
                    <option value="rentAsc">월세 낮은순</option>
                    <option value="rentDesc">월세 높은순</option>
                </select>
            </label>
            <button type="submit">검색</button>
        </form>
    </section>

    <section class="room-list-summary">
        <span id="roomListCount">매물을 불러오는 중입니다.</span>
        <button type="button" id="btnRefreshRooms">새로고침</button>
    </section>

    <section id="roomList" class="room-list-grid">
        <div class="room-list-empty">매물을 불러오는 중입니다.</div>
    </section>
</main>

<script>
    window.contextPath = "${pageContext.request.contextPath}";
</script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/room/roomList.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>
</body>
</html>

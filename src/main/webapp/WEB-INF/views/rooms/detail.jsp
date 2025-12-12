<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>룸 상세 | 룸메이트</title>

    <!-- 공통 스타일 (이미 사용 중이면 유지) -->
    <link rel="stylesheet" href="<c:url value='/resources/css/login.css'/>">
    <!-- 상세 페이지 전용 스타일 -->
    <link rel="stylesheet" href="<c:url value='/resources/css/room/room-detail.css'/>">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-container room-detail-page">

  <!-- JS에서 읽어갈 메타 데이터 -->
<div id="room-detail-data" data-room-id="${room.roomId != null ? room.roomId : roomId}"></div>
         <!-- 뒤로가기 / 상단 헤더 -->
    <div class="room-detail-topbar">
        <button type="button" id="btn-back" class="btn-link">
            ← 목록으로
        </button>

        <div class="room-detail-top-actions">
            <!-- 신고 버튼도 나중에 JS 연결 -->
            <button type="button" class="icon-btn" title="신고하기">
                🚩
            </button>
        </div>
    </div>

    <!-- 메인 헤더 (제목 / 주소 / 날짜 / 조회수 등) -->
    <header class="room-detail-header">
        <div class="room-detail-header-main">
            <h1 class="room-detail-title">${room.title}</h1>

            <div class="room-detail-meta-row">
                <div class="room-detail-meta-item">
                    📍 <span>${room.address}</span>
                </div>
                <div class="room-detail-meta-item">
                    🗓
                    <span id="room-created-at"></span>
                </div>
                <div class="room-detail-meta-item">
                    👁 <span>${room.views}</span>
                </div>
            </div>
        </div>

        <div class="room-detail-header-price">
           <div id="room-monthly" class="room-price-main">
                월세 정보 로딩중...
           </div>
           <div id="room-deposit" class="room-price-sub">
                보증금 정보 로딩중...
           </div>
        </div>
    </header>

    <!-- 전체 레이아웃: 왼쪽 상세 / 오른쪽 작성자 & 액션 -->
    <div class="room-detail-layout">
        <!-- 왼쪽: 이미지 + 정보 + 설명 -->
        <main class="room-detail-main">

            <!-- 이미지 갤러리 -->
            <section class="room-gallery">
                <div class="room-gallery-main">
                    <img id="room-main-image"
                         src="/resources/img/room/default-room.jpg"
                         alt="방 대표 이미지"
                         class="room-gallery-main-image">
                </div>
                <!-- 썸네일 (있으면) -->
                <!-- 썸네일 (JS로 필요하면 채우기) -->
                <div id="room-thumbnails" class="room-gallery-thumbs" style="display:none;">
                    <!-- JS에서 썸네일 이미지 렌더링 예정 -->
                </div>
            </section>

            <!-- 핵심 정보 카드 (룸 수, 면적, 층, 인원 등) -->
            <section class="room-summary-card">
                <div class="room-summary-grid">
                    <div class="room-summary-item">
                        <div class="room-summary-label">방 개수</div>
                        <div id="room-summary-room-count" class="room-summary-value">-</div>
                    </div>
                    <div class="room-summary-item">
                        <div class="room-summary-label">전용 면적</div>
                        <div id="room-summary-area-m2" class="room-summary-value">-</div>
                    </div>
                    <div class="room-summary-item">
                        <div class="room-summary-label">층수</div>
                        <div id="room-summary-room-floor" class="room-summary-value">-</div>
                    </div>
                    <div class="room-summary-item">
                        <div class="room-summary-label">최대 인원</div>
                        <div id="room-summary-room-max" class="room-summary-value">-</div>
                    </div>
                </div>

                <!-- 입주 가능일 / 상태 -->
                <div class="room-summary-sub">
                    <div>
                        <span class="room-summary-sub-label">입주 가능일</span>
                        <span id="room-available-from" class="room-summary-sub-value">
                            협의 가능
                        </span>
                    </div>
                    <div id="room-status-pill" class="room-status-pill room-status-OPEN">
                        모집중
                    </div>
                </div>
            </section>

            <!-- 선호 조건 / 태그 (지금은 mock or 나중에 서버 연동) -->
            <section class="room-detail-section">
                <h2 class="room-detail-section-title">선호 조건</h2>
                <div id="room-preference-chips" class="room-chip-list">
                    <!-- JS에서 칩 추가 (깔끔함, 조용함 등) -->
                </div>
            </section>

            <!-- 상세 설명 -->
            <section class="room-detail-section">
                <h2 class="room-detail-section-title">상세 정보</h2>
                <p id="room-content" class="room-detail-content">
                    상세 정보를 불러오는 중입니다...
                </p>
            </section>
        </main>

        <!-- 오른쪽: 작성자 / 액션 -->
        <aside class="room-detail-side">

            <!-- 작성자 카드 -->
            <section class="room-author-card">
                <div class="room-author-main">
                    <div class="room-author-avatar">
                        <img id="author-photo" src="/resources/img/default-user.png" alt="프로필 이미지">
                    </div>
                    <div class="room-author-info">
                        <div id="author-name" class="room-author-name">작성자</div>
                    </div>
                </div>

                <div class="room-author-extra">
                    <div class="room-author-stat">
                        👁 <span id="author-views">0</span> 회 열람
                    </div>
                    <div class="room-author-stat">
                        가입일 <span id="author-joined">-</span>
                    </div>
                </div>

                <button type="button"
                        class="btn-outline-full"
                        id="btn-view-profile">
                    프로필 보기 (추후 구현)
                </button>
            </section>

            <section class="room-detail-actions">
                <!-- 비로그인 사용자용 -->
                <div id="action-guest" style="display:none;">
                    <button type="button"
                            class="btn-primary-full"
                            onclick="window.openAuthModal && window.openAuthModal()">
                        로그인하고 문의하기
                    </button>
                </div>

                <!-- 로그인 + 작성자가 아닌 경우 -->
                <div id="action-not-owner" style="display:none;">
                    <button type="button"
                            class="btn-outline-full"
                            id="btn-like">
                        ♡ 찜하기
                    </button>
                    <button type="button"
                            class="btn-primary-full"
                            id="btn-start-chat">
                        💬 문의하기
                    </button>
                    <p class="room-detail-actions-hint">
                        문의하기는 로그인 후 이용 가능합니다.
                    </p>
                </div>

                <!-- 내가 올린 방인 경우 -->
                <div id="action-owner" style="display:none;">
                    <button type="button"
                            class="btn-outline-full"
                            id="btn-detail-edit">
                        게시글 수정
                    </button>
                    <button type="button"
                            class="btn-outline-full"
                            id="btn-detail-toggle-status">
                        모집 마감
                    </button>
                    <button type="button"
                            class="btn-danger-full"
                            id="btn-detail-delete">
                        게시글 삭제
                    </button>
                </div>
            </section>

        </aside>
    </div>
</div>

<script type="module" src="${pageContext.request.contextPath}/resources/js/common/apiClient.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/room/roomDetail.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>

</body>
</html>
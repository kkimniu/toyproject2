<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>지도에서 찾기 | 룸메이트</title>

    <!-- 분리된 CSS -->
    <link rel="stylesheet" href="<c:url value='/resources/css/room/room.css'/>">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="page-container">
    <h1 class="page-title">지도에서 찾기</h1>
    <p class="page-subtitle">지도에서 원하는 위치의 룸메이트를 찾아보세요</p>

    <div class="map-layout">
        <!-- 왼쪽: 카카오 지도 -->
        <div id="map"></div>

        <!-- 오른쪽: 상세 카드 + 근처 매물 전체 패널 -->
        <div class="room-side-panel">

            <!-- 선택된 방 카드 -->
            <div class="room-card" id="room-detail-card">

                <!-- 상단 안내 문구 (처음 들어왔을 때도 계속 보이게) -->
                <div id="room-detail-empty" class="room-card-empty">
                    지도의 마커를 클릭하면 방 상세 정보가 여기에 표시됩니다.
                </div>

                <!-- 대표 이미지 (데이터 있을 때만 JS에서 src 세팅하고 display:block 로 변경) -->
                <img id="room-image"
                     src=""
                     alt="room image"
                     class="room-card-header-image"
                     style="display:none;">

                <!-- ★ body / footer 는 항상 보이게 둔다 (display:none 제거) -->
                <div id="room-detail-body" class="room-card-body" style="display:none;">
                    <div class="room-card-title-row">
                        <!-- 초기값: 안내문 느낌으로 placeholder 텍스트 -->
                        <div class="room-card-title" id="room-title">
                            지도의 마커를 클릭해 방 정보를 확인해 보세요.
                        </div>
                        <button type="button" class="room-card-close" id="room-close-btn">×</button>
                    </div>

                    <div style="display:flex; justify-content:space-between; align-items:flex-end; gap:8px;">
                        <div>
                            <div class="room-card-address" id="room-address">
                                <!-- 초기값 비워둠 -->
                            </div>
                        </div>
                        <div style="text-align:right;">
                            <div class="room-card-price" id="room-price">월세 -</div>
                            <div class="room-card-price-sub" id="room-price-sub">
                                관리비 별도 / 공과금 별도
                            </div>
                        </div>
                    </div>

                    <div class="room-card-meta">
                        <div class="room-card-meta-item">
                            <span>입주 가능일</span>
                            <span id="room-available-from">-</span>
                        </div>
                        <div class="room-card-meta-item">
                            <span>최대 인원</span>
                            <span id="room-max-roommates">-명</span>
                        </div>
                        <div class="room-card-meta-item">
                            <span>층/면적</span>
                            <span id="room-floor-area">-층 / -㎡</span>
                        </div>
                    </div>

                    <div class="room-card-desc" id="room-content">
                        <!-- 내용은 처음엔 비워두고, 클릭 시 JS로 채움 -->
                    </div>

                    <div class="chip-list" id="room-chips">
                        <!-- 태그 칩: JS에서 채움 -->
                    </div>
                </div>

                <div class="room-card-footer" id="room-detail-footer" style="display:none;">
                    <div class="room-card-stats">
                        <div class="room-card-stat">
                            👁 <span id="room-views">0</span>
                        </div>
                        <div class="room-card-stat" id="room-status-text"></div>
                    </div>
                    <div class="room-card-actions">
                        <button type="button" class="btn btn-outline btn-heart" id="btn-favorite">
                            ♡ 찜하기
                        </button>
                        <button type="button" class="btn btn-primary" id="btn-chat">
                            문의하기
                        </button>
                    </div>
                    <button type="button" class="btn" id="btn-detail" style="margin-top:6px; width:100%;">
                        상세 보기
                    </button>
                </div>
            </div>

            <!-- 근처 다른 매물 영역 -->
            <div class="room-nearby-panel">
                <h3 class="room-nearby-title">근처 다른 매물</h3>
                <div id="nearby-list" class="room-nearby-list">
                    <p class="room-nearby-empty">근처 매물이 없습니다.</p>
                </div>
            </div>

        </div><!-- /.room-side-panel -->
    </div><!-- /.map-layout -->
</div><!-- /.page-container -->

<!-- Kakao Map JS SDK -->
<script
type="text/javascript"
src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=${kakaoJsKey}&libraries=clusterer"></script>

<!-- 분리된 JS (ES Module) -->
<script type="module" src="${pageContext.request.contextPath}/resources/js/room/roomMap.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>

</body>
</html>

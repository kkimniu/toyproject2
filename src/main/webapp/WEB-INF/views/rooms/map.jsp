<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>지도 검색 | RoomMate</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/room/room.css"/>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="page-container">
    <h1 class="page-title">지도 검색</h1>
    <p class="page-subtitle">원하는 위치의 방과 룸메이트를 지도에서 찾아보세요.</p>

    <div class="map-layout">
        <div id="map"></div>

        <div class="room-side-panel">
            <div class="room-card" id="room-detail-card">
                <div id="room-detail-empty" class="room-card-empty">
                    지도의 마커를 클릭하면 방 상세 정보가 표시됩니다.
                </div>

                <img id="room-image"
                     src=""
                     alt="방 이미지"
                     class="room-card-header-image"
                     style="display:none;">

                <div id="room-detail-body" class="room-card-body" style="display:none;">
                    <div class="room-card-title-row">
                        <div class="room-card-title" id="room-title">
                            지도의 마커를 클릭해 방 정보를 확인해 보세요.
                        </div>
                        <button type="button" class="room-card-close" id="room-close-btn">×</button>
                    </div>

                    <div style="display:flex; justify-content:space-between; align-items:flex-end; gap:8px;">
                        <div>
                            <div class="room-card-address" id="room-address"></div>
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

                    <div class="room-card-desc" id="room-content"></div>
                    <div class="chip-list" id="room-chips"></div>
                </div>

                <div class="room-card-footer" id="room-detail-footer" style="display:none;">
                    <div class="room-card-stats">
                        <div class="room-card-stat">
                            조회 <span id="room-views">0</span>
                        </div>
                        <div class="room-card-stat" id="room-status-text"></div>
                    </div>
                    <div class="room-card-actions">
                        <button type="button" class="btn btn-outline btn-heart" id="btn-favorite">
                            찜하기
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

            <div class="room-nearby-panel">
                <h3 class="room-nearby-title" id="nearby-title">검색 조건 매물</h3>
                <div id="nearby-list" class="room-nearby-list">
                    <p class="room-nearby-empty">검색 조건에 맞는 매물이 없습니다.</p>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript"
        src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=${kakaoJsKey}&libraries=clusterer"></script>
<script>
    window.contextPath = "${pageContext.request.contextPath}";
</script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/room/roomMap.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>
</body>
</html>

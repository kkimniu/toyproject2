<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8"/>
  <title>방 수정 | 룸메이트</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/room/room-form.css"/>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/room/room-edit.css"/>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<main class="room-form-main">
  <div class="page-top">
    <button type="button" id="btn-back" class="btn-link">← 마이페이지로</button>
  </div>

  <h1 class="page-title">방 수정하기</h1>
  <p class="page-subtitle">룸메이트를 찾기 위한 방 정보를 입력해주세요</p>

  <form id="roomEditForm" class="form" novalidate>
    <input type="hidden" id="roomId" value="${roomId}" />
    <section class="card">
      <div class="card-head">
        <h2 class="card-title">기본 정보</h2>
        <p class="card-desc">방의 기본 정보를 입력해주세요</p>
      </div>

      <div class="field">
        <label class="label">제목 <span class="req">*</span></label>
        <input id="roomTitle" class="input" type="text" placeholder="예: 강남역 도보 5분, 투룸 룸메이트 구함"/>
      </div>

      <div class="field">
        <label class="label">상세 설명 <span class="req">*</span></label>
        <textarea id="content" class="textarea" rows="4" placeholder="방/생활 환경을 자세히 적어주세요"></textarea>
      </div>
    </section>

    <section class="card">
      <div class="card-head">
        <h2 class="card-title">위치 정보</h2>
        <p class="card-desc">주소 검색으로 선택하면 서버에서 좌표를 자동으로 계산해 저장합니다</p>
      </div>

      <div class="field">
        <label class="label">주소 <span class="req">*</span></label>
        <div class="addr-row">
          <input id="address" class="input" type="text" placeholder="주소 검색을 눌러주세요" readonly/>
          <button type="button" id="btnAddrSearch" class="btn btn-primary">주소 검색</button>
        </div>
        <label class="label" style="margin-top:10px;">상세주소(선택)</label>
        <input id="addressDetail" class="input" type="text" placeholder="상세주소(선택) 예) 101동 1004호" />
        <!-- 서버로 같이 보낼 값들(선택) -->
        <input type="hidden" id="legalDong"/>
        <input type="hidden" id="landNumber"/>
      </div>
    </section>

    <section class="card">
      <div class="card-head">
        <h2 class="card-title">방 정보</h2>
        <p class="card-desc">금액/면적은 0 이상</p>
      </div>

      <div class="grid-2">
        <div class="field">
          <label class="label">월세 <span class="req">*</span></label>
          <input id="monthlyRent" class="input" type="number" min="0" step="1" placeholder="500000"/>
          <p class="help">현재 상세페이지가 /10000 해서 만원으로 보여주니, DB 저장 단위(원/만원)를 프로젝트 기준으로 통일하세요.</p>
        </div>
        <div class="field">
          <label class="label">보증금 <span class="req">*</span></label>
          <input id="deposit" class="input" type="number" min="0" step="1" placeholder="10000000"/>
        </div>
      </div>

      <div class="grid-3">
        <div class="field">
          <label class="label">면적(m²) <span class="req">*</span></label>
          <input id="areaM2" class="input" type="number" min="0" step="0.1" placeholder="25.0"/>
        </div>
        <div class="field">
          <label class="label">층수</label>
          <input id="floor" class="input" type="number" min="0" step="1" placeholder="3"/>
        </div>
        <div class="field">
          <label class="label">최대 룸메이트</label>
          <input id="maxRoommates" class="input" type="number" min="0" step="1" placeholder="2"/>
        </div>
      </div>

      <div class="grid-2">
        <div class="field">
          <label class="label">방 타입 <span class="req">*</span></label>
          <select id="roomTypeId" class="select">
            <option value="">선택</option>
          </select>
        </div>

        <div class="field">
          <label class="label">입주 가능일</label>
          <input id="availableFrom" class="input" type="date"/>
          <p class="help">미입력 시 협의 가능</p>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card-head">
        <h2 class="card-roomTitle">이미지 (MVP: 미리보기만)</h2>
        <p class="card-desc">업로드 API 연결 시 imageUrls로 전송</p>
      </div>

      <div class="upload-wrap">
        <label class="upload-box" for="photoInput">
          <div class="upload-icon">⬆️</div>
          <div class="upload-text">이미지 추가</div>
          <input id="photoInput" type="file" accept="image/*" multiple hidden/>
        </label>
        <div class="preview-grid" id="previewGrid"></div>
      </div>
    </section>

    <div class="bottom-actions">
      <button type="button" id="btnCancel" class="btn btn-ghost">취소</button>
      <button type="submit" class="btn btn-primary">방 수정하기</button>
    </div>
  </form>
</main>

<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/room/roomEdit.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/auth/login.js"></script>
</body>
</html>

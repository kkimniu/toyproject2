# Roommate Toy Project

## Development Notes

- [Admin page backlog](docs/admin-backlog.md)

룸메이트와 방 매물을 등록하고, 관심 있는 방 작성자에게 실시간으로 문의할 수 있는 Spring MVC 기반 웹 애플리케이션입니다.

## 기술 스택

| 영역 | 기술 |
| --- | --- |
| Backend | Java 17, Spring MVC 5.3.39, Spring Security 5.8.11, Spring WebSocket/STOMP |
| Database | MySQL 8.x, MyBatis 3.5.16, MyBatis-Spring 2.1.2, Apache DBCP2 |
| View | JSP, JSTL, Vanilla JavaScript, CSS |
| Auth | JWT access/refresh token, BCrypt |
| API/JSON | Jackson, jackson-datatype-jsr310 |
| File | commons-fileupload, commons-io |
| External API | Kakao Local/Map API |
| Build | Maven, WAR packaging |
| Test/Logging | JUnit 4, Spring Test, SLF4J, Logback |

## 주요 기능

### 회원/인증

- 회원가입, 로그인, JWT 기반 인증
- access token/refresh token 발급 및 갱신
- Spring Security 기반 API 보호
- 내 정보 조회 및 프로필 관리

### 방 매물

- 방 등록, 수정, 상세 조회, 목록/지도 조회
- 방 타입, 월세, 보증금, 면적, 층수, 입주 가능일, 최대 룸메이트 수 관리
- Kakao 주소 검색 API를 통한 위도/경도 변환
- 이미지 업로드 및 임시 파일 사용 처리
- 관심 목록 및 조회수 처리

### 채팅

- 방 상세에서 작성자에게 문의하기
- 채팅방 목록/상세 화면
- Spring WebSocket + STOMP 기반 실시간 메시지 송수신
- JWT 기반 WebSocket 인증
- 읽음 위치 저장 및 안 읽은 메시지 수 표시
- 채팅 알림 설정
- 채팅 시간 표시 형식 개선
  - 예: `2026년 5월 10일 18시 28분 19초`
- 채팅방 나가기 정책
  - 한쪽이 나가면 해당 사용자의 목록에서는 방이 숨겨집니다.
  - 남아 있는 사용자의 목록에는 `상대방이 나갔습니다.`가 표시됩니다.
  - 양쪽 모두 나가면 양쪽 목록에서 방이 보이지 않습니다.
  - 나간 사용자가 다시 문의하면 같은 채팅방을 재사용하지만, 나간 시점 이전 대화는 보이지 않습니다.
  - 나가지 않은 사용자는 기존 대화 기록을 계속 볼 수 있습니다.
  - 나간 사용자에게는 새 메시지 알림을 보내지 않습니다.

### 알림/신고

- 채팅 알림 저장
- 회원, 매물, 채팅, 커뮤니티 게시글, 커뮤니티 댓글 신고 접수
- 신고 처리 결과 알림 저장 및 헤더 알림 확인
- 사용자의 내 신고 처리 결과 조회

### 커뮤니티

- 커뮤니티 게시글 목록/상세 조회
- 로그인 회원의 커뮤니티 게시글 작성, 수정, 삭제
- 댓글 및 대댓글 작성/삭제
- 게시글 조회수는 회원당 1회 기준으로 집계
- 게시글/댓글 신고 연동

### 공지사항/카테고리

- 사용자 공지사항 목록 및 상세 조회
- 관리자 공지사항 등록, 수정, 삭제
- 관리자 공통 카테고리 관리
  - 직업
  - 취미
  - 선호
  - 반려동물

### 관리자

- 관리자 페이지 탭 기반 화면 구성
- 회원 목록 조회, 검색, 필터, 페이지 크기 조절
- 회원 상태 변경
  - 일반 회원 정지/해제
  - 정지 횟수 누적
  - 반복 제재 회원 강조 표시
  - 일반 회원 탈퇴 처리
- 관리자 권한 관리
  - 상위 관리자의 관리자 승격 및 권한 회수
  - 상위 관리자 계정 보호
- 신고 목록 조회, 검색, 필터, 처리
- 신고 처리 결과 및 안내 문구 저장
- 신고 유형별 직접 조치
  - 회원 신고 대상 정지/탈퇴
  - 매물 신고 대상 방 삭제
  - 채팅 신고 대상 채팅방 삭제
  - 커뮤니티 게시글/댓글 블라인드 또는 삭제
- 관리자 대시보드
  - 전체 회원, 정지 회원, 대기 신고, 처리 완료 신고 요약
  - 확인 필요 항목 표시
  - 신고 3건 이상 누적 제재 후보 표시
  - 제재 후보 선택 정지/탈퇴 일괄 처리
  - 최근 7일 신고 접수/처리 추이 표시
- 관리자 작업 로그 저장 및 조회
- 작업 로그 검색/필터 및 대상 상세 진입

## 프로젝트 구조

```text
src/main/java/com/roommate
├── admin             # 관리자 API/서비스/DTO
├── common
│   ├── config          # MVC, WebSocket, JDBC 설정
│   ├── exception       # 공통 예외/에러 응답
│   ├── jwt             # JWT 발급/검증
│   ├── security        # Spring Security 인증 처리
│   └── websocket       # STOMP/JWT 채팅 인증
├── domain
│   ├── admin           # 관리자 작업 로그 저장소
│   ├── auth            # 로그인/토큰
│   ├── chat            # 채팅방/메시지
│   ├── community       # 커뮤니티 게시글/댓글
│   ├── favorite        # 관심 방
│   ├── file            # 파일 업로드
│   ├── member          # 회원/프로필
│   ├── notice          # 공지사항
│   ├── notification    # 알림
│   ├── report          # 신고
│   └── room            # 방 매물
└── external
    └── kakao           # Kakao API 연동
```

```text
src/main/resources
├── mapper              # MyBatis XML mapper
├── sql                 # 초기 스키마 및 수동 적용 SQL
└── application.properties.example
```

```text
src/main/webapp
├── WEB-INF/views       # JSP 화면
└── resources
    ├── css
    └── js
```

## 실행

Maven이 설치된 환경에서 WAR를 빌드합니다.

```bash
mvn clean package
```

이 프로젝트는 WAR 패키징 프로젝트이므로 Tomcat 같은 Servlet 컨테이너에 배포해서 실행합니다.

## 최근 반영 내용

- 관리자 커뮤니티 게시글/댓글 블라인드 및 삭제 처리
- 관리자 신고 목록에서 회원 탈퇴, 방 삭제, 채팅방 삭제 직접 처리
- 관리자 대시보드에 신고 누적 제재 후보, 확인 필요 항목, 최근 7일 신고 추이 추가
- 제재 후보 선택 정지/탈퇴 일괄 처리
- 공지사항 및 공통 카테고리 관리 기능
- 커뮤니티 게시글, 댓글, 대댓글, 신고 연동
- 회원당 1회 기준 게시글 조회수 집계
- 삭제/탈퇴/정지 회원의 채팅 표시 및 접근 정책 개선
- 사용자 신고 답변 알림 및 내 신고 처리 결과 확인

## 검증 메모

- `mvn test` 통과
- `node --check`로 관리자 주요 JavaScript 문법 체크 완료
  - `dashboardSummary.js`
  - `memberList.js`
  - `reportList.js`
- 관리자 신고/대시보드 MyBatis 집계 SQL을 로컬 DB에서 확인

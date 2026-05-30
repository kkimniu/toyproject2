# Deployment Guide

이 문서는 Roommate Toy Project를 Tomcat 환경에 배포하기 위한 체크리스트다.

## 1. 서버 요구사항

- Java 17
- Maven 3.x
- MySQL 8.x
- Tomcat 9.x
- Kakao Local API REST key
- Kakao JavaScript key
- 업로드 파일을 저장할 서버 디렉터리

## 2. 운영 설정 파일

`src/main/resources/application.properties.example`을 기준으로 운영 서버에 `application.properties`를 만든다.

필수 설정:

```properties
jwt.secret.key=운영용_랜덤_시크릿
jwt.access.expiration.time=3600000
jwt.refresh.expiration.time=604800000

db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/roommate?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
db.username=roommate_user
db.password=운영_DB_비밀번호

kakao.map.api.key=Kakao_REST_API_KEY
kakao.js.key=Kakao_JavaScript_KEY
kakao.local.base-url=https://dapi.kakao.com
kakao.ka.origin=roommate-service

file.upload.root-path=/var/roommate/uploads
```

주의:

- `application.properties`는 Git에 올리지 않는다.
- JWT secret은 개발용 값을 재사용하지 않는다.
- `file.upload.root-path`는 Tomcat 프로세스가 쓰기 가능한 디렉터리로 지정한다.

## 3. DB 준비

DB와 계정을 생성한다.

```sql
CREATE DATABASE roommate
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

CREATE USER 'roommate_user'@'%' IDENTIFIED BY '운영_DB_비밀번호';
GRANT ALL PRIVILEGES ON roommate.* TO 'roommate_user'@'%';
FLUSH PRIVILEGES;
```

신규 DB라면 `schema.sql`을 먼저 적용한다.

```bash
mysql -u roommate_user -p roommate < src/main/resources/sql/schema.sql
```

기존 DB를 업데이트하는 경우 아래 SQL을 순서대로 적용한다.

```text
2026-05-10_expand_room_price_columns.sql
2026-05-16_add_report_status.sql
2026-05-17_add_admin_action_log.sql
2026-05-17_add_report_resolution_details.sql
2026-05-17_add_super_admin_role.sql
2026-05-18_add_admin_member_delete_action.sql
2026-05-18_add_member_ban_count.sql
2026-05-19_add_member_report_type.sql
2026-05-20_add_notices.sql
2026-05-22_add_community_posts.sql
2026-05-23_add_community_blind_flags.sql
2026-05-23_add_community_comment_replies.sql
2026-05-23_add_community_comments_reports.sql
2026-05-23_add_community_post_views.sql
2026-05-30_add_admin_dashboard_settings.sql
```

PowerShell에서는 `<` 리다이렉션 대신 다음 방식을 쓴다.

```powershell
Get-Content .\src\main\resources\sql\2026-05-30_add_admin_dashboard_settings.sql | mysql -u roommate_user -p roommate
```

## 4. 빌드

테스트와 WAR 빌드를 실행한다.

```bash
mvn test
mvn clean package
```

빌드 산출물:

```text
target/toyproject2-0.0.1-SNAPSHOT.war
```

## 5. Tomcat 배포

1. 기존 애플리케이션을 중지한다.
2. 기존 WAR와 exploded 디렉터리를 백업한다.
3. 새 WAR를 Tomcat `webapps`에 배치한다.
4. 컨텍스트 경로를 `/`로 쓸 경우 WAR 이름을 `ROOT.war`로 둔다.
5. Tomcat을 시작한다.
6. 로그에서 Spring context 초기화와 DB 연결 오류가 없는지 확인한다.

## 6. 배포 후 스모크 테스트

아래 순서로 확인한다.

- 회원가입
- 로그인
- 내 정보 조회
- 방 등록
- 방 상세 조회
- 지도 조회
- 관심 방 등록/해제
- 채팅방 생성
- 실시간 채팅 송수신
- 채팅방 나가기/재입장
- 회원 신고
- 매물 신고
- 채팅 신고
- 커뮤니티 게시글 작성/수정/삭제
- 커뮤니티 댓글/대댓글 작성/삭제
- 게시글/댓글 신고
- 공지사항 목록/상세
- 관리자 로그인
- 관리자 회원 목록/검색/필터
- 관리자 신고 목록/검색/필터
- 신고 처리 및 신고자 알림 확인
- 커뮤니티 블라인드/삭제
- 방 삭제, 채팅방 삭제, 회원 정지/탈퇴
- 관리자 대시보드 요약/제재 후보/신고 추이
- 관리자 공지사항/카테고리 관리
- 관리자 작업 로그 조회

## 7. 배포 전 최종 확인

- 운영 `application.properties`가 배포 WAR 또는 Tomcat classpath에 포함되는지 확인
- DB 마이그레이션이 모두 적용됐는지 확인
- 업로드 디렉터리가 존재하고 쓰기 가능한지 확인
- Kakao 키의 허용 도메인과 JavaScript 키 설정 확인
- 관리자 계정과 상위 관리자 계정 상태 확인
- 서버 시간대가 `Asia/Seoul` 기준으로 동작하는지 확인
- `target/`, `uploads/`, 실제 운영 설정 파일이 Git에 포함되지 않았는지 확인

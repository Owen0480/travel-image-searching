# DB 테이블 확인 및 설계 (2번)

## 1. 현재 DB·테이블 확인 결과

- **DB**: MariaDB `travel` (Spring Boot `application.yml`: `jdbc:mariadb://localhost:3306/travel`)
- **JPA**: `ddl-auto: update` → 엔티티 기준으로 테이블 자동 생성

### 이미 있는 테이블(엔티티)

| 테이블명 | 용도 |
|----------|------|
| `users` | 사용자 |
| `saved_conversations` | 저장된 대화 |
| `travel_style_results` | 여행 스타일 결과 |
| `refresh_tokens` (추정) | JWT 리프레시 토큰 |

### 아직 없는 테이블

- **place** (장소 정보) → 없음  
- **place_photo / tour_photo** (사진–장소 매핑 + 이미지) → 없음  

→ **2번(DB 테이블 설계)부터 진행하면 됨.**

---

## 2. 테이블 설계 제안

### 2-1. `place` (장소)

- **출처**: `place.csv`  
- **역할**: VISIT_AREA_ID 기준 장소 한 건 (장소명, 주소, 좌표, 권역 등)

| 컬럼(예시) | 타입 | 비고 |
|------------|------|------|
| id | BIGINT PK AUTO_INCREMENT | 자체 PK |
| visit_area_id | VARCHAR(20) UNIQUE NOT NULL | CSV의 VISIT_AREA_ID |
| visit_area_nm | VARCHAR(200) | 장소명 |
| road_nm_addr | VARCHAR(500) | 도로명주소 |
| lotno_addr | VARCHAR(500) | 지번주소 |
| x_coord | DOUBLE | 경도 |
| y_coord | DOUBLE | 위도 |
| sgg_cd | VARCHAR(20) | 시군구코드 (권역 등 활용) |
| created_at / updated_at | DATETIME | 감사용(선택) |

### 2-2. `place_photo` (장소–사진 매핑 + 이미지)

- **출처**: `tour.csv` + 이미지 파일  
- **역할**: 한 장의 사진 = 한 행. 이미지는 BLOB 또는 경로 중 택일.

| 컬럼(예시) | 타입 | 비고 |
|------------|------|------|
| id | BIGINT PK AUTO_INCREMENT | 자체 PK |
| photo_file_nm | VARCHAR(255) NOT NULL | 파일명 (예: f00033801003p0001.jpg) |
| visit_area_id | VARCHAR(20) NOT NULL | place.visit_area_id와 매핑 |
| visit_area_nm | VARCHAR(200) | 표시용 장소명 (중복 저장 허용) |
| image_data | LONGBLOB | 이미지 바이너리 (BLOB로 저장할 경우) |
| photo_file_frmat | VARCHAR(10) | jpg 등 |
| photo_file_dt | DATETIME | 촬영일시 |
| x_coord, y_coord | DOUBLE | 촬영 위치 |
| created_at / updated_at | DATETIME | 감사용(선택) |

- **BLOB vs 경로**:  
  - BLOB: DB 한 곳에 통합, 백업/이전 시 유리.  
  - 경로: DB에는 경로만 저장, 파일은 스토리지에 보관.  
  - 결정: 이전 대화에서 **이미지 BLOB로 저장**하기로 함.

---

## 3. 다음 단계

1. **Spring Boot에 엔티티 추가**  
   - `Place.java`, `PlacePhoto.java` 생성  
   - `ddl-auto: update`로 `place`, `place_photo` 테이블 생성  
2. **데이터 이전**  
   - place.csv → `place` INSERT  
   - tour.csv + images 폴더 → `place_photo` INSERT (이미지 BLOB 포함)  
3. **FastAPI 수정**  
   - 이미지 추천만 **3306(도커 DB)** 연결. 나머지(Spring 등)도 **3306** 사용 가능.
   - FastAPI `backend-fastapi/.env`에 아래 추가 시 추천 서비스가 3306의 `place`/`place_photo` 사용:
     - `MARIADB_HOST=localhost`
     - `MARIADB_PORT=3306`
     - (선택) `MARIADB_USER`, `MARIADB_PASSWORD`, `MARIADB_DATABASE` — 없으면 root/1234/travel
   - `MARIADB_HOST`를 비우거나 없으면 기존처럼 CSV + images 폴더 사용.

이 문서는 “2번: DB 테이블 설계” 확인용이며, 실제 구현은 위 3단계 순서로 진행하면 됩니다.

---

## 4. 직접 테이블 만드는 방법 (어디서 / 어떻게)

### 어디서 만드나요?

- **DB**: MariaDB  
- **접속 정보** (Spring Boot `application.yml` 기준)  
  - 호스트: `localhost`  
  - 포트: `3306`  
  - DB 이름: `travel`  
  - 사용자: `root` (또는 본인 설정)  
  - 비밀번호: `1234` (또는 본인 설정)

**사용할 수 있는 도구 (택 1)**

| 도구 | 용도 |
|------|------|
| **HeidiSQL** | Windows에서 많이 씀. MariaDB/MySQL 무료 클라이언트. |
| **DBeaver** | 크로스 DB 무료 클라이언트. |
| **MySQL Workbench** | MySQL/MariaDB 공식 계열. |
| **VS Code 확장** | "MySQL" 또는 "Database Client" 등으로 접속 후 쿼리 실행. |
| **명령줄** | `mariadb -u root -p -h localhost travel` 로 접속 후 SQL 입력. |

**DBeaver 사용 시:**  
1. 상단 **데이터베이스 → 새 데이터베이스 연결** → **MariaDB** 선택.  
2. 호스트: `localhost`, 포트: `3306`, DB: `travel`, 사용자/비밀번호 입력 후 **테스트 연결** → **완료**.  
3. 왼쪽에서 `travel` DB 펼친 뒤 **SQL 편집기 → 새 SQL 스크립트** (또는 `Ctrl+]`).  
4. `docs/sql/create-place-tables.sql` 내용 붙여넣고 **실행** (Ctrl+Enter 또는 재생 버튼).  
5. 테이블 목록에서 `place`, `place_photo` 보이면 완료.

→ 위 중 하나로 **localhost:3306**에 접속해서 **데이터베이스 `travel`** 을 선택한 뒤, 아래 SQL을 실행하면 됩니다.

### 어떻게 만드나요?

1. 위 도구로 MariaDB에 접속한다.  
2. 데이터베이스 **`travel`** 을 선택(또는 USE travel)한다.  
3. **아래 4.1절 SQL**을 복사해 쿼리 창에 붙여넣고 실행한다.  
   - 또는 프로젝트에 있는 **`docs/sql/create-place-tables.sql`** 파일을 열어서 그 내용을 실행해도 됩니다.

실행이 끝나면 `place`, `place_photo` 테이블이 생성됩니다.  
테이블 목록에서 `place` / `place_photo`가 보이면 성공입니다.

### 4.1. 실행용 SQL (복사해서 사용)

```sql
-- 데이터베이스 선택 (이미 travel로 접속했으면 생략 가능)
USE travel;

-- 장소 테이블
CREATE TABLE IF NOT EXISTS place (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    visit_area_id VARCHAR(20) NOT NULL UNIQUE,
    visit_area_nm VARCHAR(200),
    road_nm_addr VARCHAR(500),
    lotno_addr VARCHAR(500),
    x_coord DOUBLE,
    y_coord DOUBLE,
    sgg_cd VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 장소-사진 테이블 (이미지 BLOB)
CREATE TABLE IF NOT EXISTS place_photo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    photo_file_nm VARCHAR(255) NOT NULL,
    visit_area_id VARCHAR(20) NOT NULL,
    visit_area_nm VARCHAR(200),
    image_data LONGBLOB,
    photo_file_frmat VARCHAR(10),
    photo_file_dt DATETIME,
    x_coord DOUBLE,
    y_coord DOUBLE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 나중에 CSV 넣을 때 조회 속도용 (선택)
CREATE INDEX idx_place_photo_visit_area_id ON place_photo(visit_area_id);
CREATE INDEX idx_place_photo_photo_file_nm ON place_photo(photo_file_nm);
```

동일 SQL 파일: **`docs/sql/create-place-tables.sql`**

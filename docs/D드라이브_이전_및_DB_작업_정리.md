# D드라이브 이전 + DB 작업 정리 (AI/본인 참고용)

> 프로젝트를 **C드라이브 → D드라이브**로 옮긴 뒤, 또는 다른 PC에서 열었을 때  
> "지금 무슨 상태인지" 알 수 있도록 정리한 문서입니다.

---

## 1. 경로 변경 (D로 옮겼을 때)

- **이전**: `C:\Users\human-12\Desktop\travel_pj`
- **이후**: **D드라이브** 어딘가 (예: `D:\travel_pj` 또는 `D:\projects\travel_pj`)
- Cursor/IDE에서는 **D:\...\travel_pj** 폴더를 열어서 작업합니다.
- 스크립트·설정에 **C드라이브 절대경로**가 있으면 **D 기준 경로**로 바꿔서 사용하세요.

---

## 2. DB 구조 (이미 해 둔 것)

- **이미지 추천용 DB**: **도커 MariaDB**, 포트 **3306**, 데이터는 **D드라이브**에 저장
  - 컨테이너: `travel-mariadb`
  - 볼륨: `D:\docker\mariadb\travel-data` → 컨테이너 내부 `/var/lib/mysql`
  - DB 이름: `travel` / 사용자: `root` / 비밀번호: `1234`
- **테이블**: `place`, `place_photo`
  - 생성 SQL: `docs/sql/create-place-tables.sql`
  - DBeaver 등에서 **localhost:3306** 로 접속 후 위 SQL 실행하면 됨.
- **Spring Boot(로그인·대화 등)** 는 **3306** (로컬 MariaDB) 그대로 사용.  
  **이미지 추천만 3306** 사용.

---

## 3. 데이터 넣기 (INSERT)

- **스크립트**: `backend-fastapi/scripts/insert_place_data.py`
- **데이터 소스**: **국내 여행로그 데이터** 폴더 (프로젝트 루트 아래)
  - `place` ← `국내 여행로그 데이터(수도권|동부권|서부권|제주도 및 도서지역)/Sample/02.라벨링데이터/csv/tn_visit_area_info_방문지정보_{A|B|C|D}.csv`
  - `place_photo` ← 위 지역별 `tn_tour_photo_관광사진_{A|B|C|D}.csv`
  - 이미지 파일 ← 각 지역별 `.../Sample/01.원천데이터/photo/` (DB에는 경로만 저장 권장)
- **실행 전**:
  - 도커에서 `travel-mariadb` 컨테이너 실행
  - `place`, `place_photo` 테이블 생성 완료 (이미 있으면 `docs/sql/alter-place_photo-add-image_path.sql` 로 image_path 컬럼 추가)
- **실행 예시** (backend-fastapi 폴더에서):
  ```powershell
  $env:MARIADB_PORT="3306"
  $env:STORE_IMAGES="0"   # 이미지 BLOB 안 넣고 image_path만 저장 (권장)
  python scripts/insert_place_data.py
  ```
- **환경변수**: `TRAVEL_DATA_ROOT` 없으면 기본값 = 프로젝트 루트(`travel_pj`). 다른 경로에 데이터 두었으면 설정.

---

## 4. FastAPI에서 3306 사용

- **설정**: `backend-fastapi/.env` 에 아래 추가 시 이미지 추천이 **3306 도커 DB**를 사용합니다.
  ```env
  MARIADB_HOST=localhost
  MARIADB_PORT=3306
  ```
- 없으면 기존처럼 CSV + `images/` 폴더 기준으로 동작합니다.
- **코드**: `backend-fastapi/app/services/recommend_service.py` 에서  
  `MARIADB_HOST` 가 있으면 3306에 연결해 `place`, `place_photo` 조회.

---

## 5. 아직 안 한 것 (하기로 한 방향)

1. **이미지 → D드라이브에만 저장, DB에는 경로만**
   - 예: 이미지 파일은 `D:\travel_images\` 같은 곳에 두고,  
     `place_photo` 테이블에는 `image_path` (또는 URL) 컬럼으로 경로만 저장.
2. **INSERT 스크립트**: 이미지 파일을 D로 복사 + DB에 경로만 넣도록 수정.
3. **FastAPI**: DB에서 `image_path` 읽어서 그 경로에서 이미지 로드해 임베딩 계산.
4. **로컬 정리**: 위까지 완료 후 `backend-fastapi/images`, `backend-fastapi/data` 삭제 가능.

---

## 6. 도커 MariaDB 다시 띄우는 방법 (D에서 작업할 때)

```powershell
docker run -d --name travel-mariadb -e MARIADB_ROOT_PASSWORD=1234 -e MARIADB_DATABASE=travel -p 3306:3306 -v "D:\docker\mariadb\travel-data:/var/lib/mysql" mariadb:10.6
```

이미 컨테이너가 있으면: `docker start travel-mariadb`

---

## 7. 이미지 추천 화면 (imageSeaching.html)

- **위치**: `travel-frontend/public/imageSeaching.html`
- **동작**: 이미지 업로드 → Spring Boot(8080) `/api/v1/recommend/analyze` → FastAPI(8000) 분석 → 결과 표시
- **표시 내용**: 업로드 후 "유사 여행지 N곳 추천" 문구, 취향 입력란(preference) 전송, 추천 결과별 가이드 문구(상단 설명 + 카드 내 요약)

---

## 8. 참고 문서

- 테이블 설계·직접 생성 방법: `docs/DB테이블_확인_및_설계.md`
- 테이블 생성 SQL: `docs/sql/create-place-tables.sql`

---

*마지막 정리: 프로젝트 D드라이브 이전 후, 이미지 추천 DB는 3306(도커·D 볼륨), 이미지 파일은 추후 D에 두고 DB에는 경로만 저장하기로 함.*

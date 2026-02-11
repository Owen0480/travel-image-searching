# 국내 여행로그 데이터 DB에 넣기

국내 여행로그 데이터(수도권/동부권/서부권/제주도 및 도서지역)를 MariaDB `place`, `place_photo` 테이블에 넣는 방법입니다.

---

## 1. 준비 (한 번만)

### 1) 도커 MariaDB 실행

- 포트 **3306:3306** 으로 떠 있어야 함.
- 확인: `docker ps` 에 mariadb(또는 travel-mariadb) 있는지 확인.

### 2) 테이블 생성

- DBeaver/HeidiSQL로 **localhost:3306**, DB **travel** 접속.
- `docs/sql/create-place-tables.sql` 실행 → `place`, `place_photo` 생성.
- 예전에 테이블만 만들어 둔 경우: `docs/sql/alter-place_photo-add-image_path.sql` 실행해서 `image_path` 컬럼 추가.

### 3) 데이터 폴더 위치

- 프로젝트 루트(`travel_pj`) 아래에 아래 폴더들이 있어야 함.
  - `국내 여행로그 데이터(수도권)`
  - `국내 여행로그 데이터(동부권)`
  - `국내 여행로그 데이터(서부권)`
  - `국내 여행로그 데이터(제주도 및 도서지역)`
- 각 폴더 안에 `Sample\02.라벨링데이터\csv\`, `Sample\01.원천데이터\photo\` 구조가 있어야 함.

---

## 2. 데이터 넣기 (실행)

### 방법 A: .env 쓰는 경우 (권장)

`backend-fastapi/.env` 에 이미 다음이 있으면:

```env
MARIADB_HOST=localhost
MARIADB_PORT=3306
```

**backend-fastapi** 폴더에서 아래만 실행하면 됨.

```powershell
cd C:\Users\human-12\Desktop\travel_pj\backend-fastapi

python scripts/insert_place_data.py
```

(스크립트가 .env 를 읽어서 DB 접속함.)

### 방법 B: 터미널에서 포트만 지정

```powershell
cd C:\Users\human-12\Desktop\travel_pj\backend-fastapi

$env:MARIADB_PORT="3306"
python scripts/insert_place_data.py
```

---

## 3. 옵션 (필요할 때만)

- **이미지 BLOB 안 넣고 경로만 넣고 싶을 때** (용량 절약, 권장):
  ```powershell
  $env:STORE_IMAGES="0"
  python scripts/insert_place_data.py
  ```
- **실제 이미지 파일이 있는 행만 넣고 싶을 때**:
  ```powershell
  $env:ONLY_EXISTING_IMAGES="1"
  python scripts/insert_place_data.py
  ```
- **데이터가 다른 드라이브/경로에 있을 때** (예: D:\data):
  ```powershell
  $env:TRAVEL_DATA_ROOT="D:\data"
  python scripts/insert_place_data.py
  ```
  (기본값은 프로젝트 루트 = `travel_pj` 폴더)

---

## 4. 실행 후 확인

- 터미널에 `place: N건 INSERT`, `place_photo: M건 INSERT`, `완료.` 나오면 성공.
- DBeaver 등에서 `travel` DB → `place`, `place_photo` 테이블 조회해서 행 수 확인하면 됨.

---

## 5. 에러 나올 때

- **연결 실패**  
  - 도커 MariaDB가 떠 있는지, `.env`의 `MARIADB_PORT=3306` 이 맞는지 확인.
- **파일 없음 / 읽을 CSV 없음**  
  - 프로젝트 루트에 `국내 여행로그 데이터(수도권)` 등 4개 폴더가 있는지,  
  - 각 폴더 안에 `Sample\02.라벨링데이터\csv\tn_visit_area_info_방문지정보_*.csv` 등이 있는지 확인.
- **pymysql / pandas 없음**  
  - `pip install pymysql pandas python-dotenv` 후 다시 실행.

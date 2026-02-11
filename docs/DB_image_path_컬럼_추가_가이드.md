# DB에 image_path 컬럼 추가하기 (이미 테이블 만든 경우)

> `place_photo` 테이블은 이미 만들어져 있는데, **image_path** 컬럼만 추가하고 싶을 때 사용하는 가이드입니다.

---

## 0. 사전 확인

### 0-1. 이 작업이 필요한 경우

- 예전에 `docs/sql/create-place-tables.sql` 로 **image_path 없이** `place_photo` 테이블을 만든 경우
- 지금은 **국내 여행로그 데이터** 기준으로 이미지 경로만 DB에 넣고, 파일은 그대로 두고 싶은 경우

### 0-2. 도커 MariaDB가 켜져 있어야 함

- **이미지 추천용 DB**는 포트 **3306** 의 도커 MariaDB입니다.
- 터미널에서 확인:
  ```powershell
  docker ps
  ```
  - 목록에 `travel-mariadb` 가 있어야 합니다.
- 없으면 먼저 실행:
  ```powershell
  docker start travel-mariadb
  ```
  - 한 번도 만들지 않았다면:
  ```powershell
  docker run -d --name travel-mariadb -e MARIADB_ROOT_PASSWORD=1234 -e MARIADB_DATABASE=travel -p 3306:3306 -v "D:\docker\mariadb\travel-data:/var/lib/mysql" mariadb:10.6
  ```

### 0-3. 접속 정보 (꼭 맞춰서 입력)

| 항목 | 값 |
|------|-----|
| Host | `localhost` (또는 `127.0.0.1`) |
| Port | **3306** |
| Database | `travel` |
| User | `root` |
| Password | `1234` |

---

## 1. DBeaver로 추가하기 (GUI)

### 1-1. DBeaver 실행 후 연결 만들기

1. **데이터베이스** → **새 데이터베이스 연결** (또는 상단 플러그 아이콘).
2. **MariaDB** 또는 **MySQL** 선택 → 다음.
3. 아래처럼 입력:
   - **Host**: `localhost`
   - **Port**: `3306`
   - **Database**: `travel`
   - **Username**: `root`
   - **Password**: `1234`
4. **테스트 연결** 눌러서 성공하면 **완료**.

### 1-2. travel DB 선택

- 왼쪽 트리에서 **travel** 데이터베이스를 클릭해 선택합니다.

### 1-3. SQL 실행

1. **SQL 편집기** 열기:  
   - `travel` 우클릭 → **SQL 편집기** → **새 SQL 스크립트**  
   - 또는 상단 **SQL** 버튼 클릭.
2. 아래 SQL **전부** 붙여넣기:
   ```sql
   USE travel;

   ALTER TABLE place_photo
   ADD COLUMN image_path VARCHAR(500) NULL
   COMMENT '국내 여행로그 데이터 기준 상대 경로'
   AFTER image_data;
   ```
3. **실행** 버튼 클릭 (Ctrl+Enter 또는 번개 아이콘).
4. 하단 **메시지**에 `완료` 또는 `Success` 나오면 성공.

### 1-4. 컬럼 추가 여부 확인

1. 왼쪽에서 **travel** → **테이블** → **place_photo** 펼치기.
2. **place_photo** 우클릭 → **테이블** → **컬럼** (또는 **테이블 편집**).
3. 목록에 **image_path**, 타입 **VARCHAR(500)** 이 보이면 정상 추가된 것입니다.

---

## 2. HeidiSQL로 추가하기 (GUI)

### 2-1. 세션 만들기

1. HeidiSQL 실행 → **새 세션** (또는 기존 세션 선택).
2. **네트워크 유형**: MySQL (TCP/IP).
3. 입력:
   - **호스트명 / IP**: `127.0.0.1`
   - **사용자**: `root`
   - **비밀번호**: `1234`
   - **포트**: `3306`
4. **열기**로 접속.

### 2-2. travel 선택 후 쿼리 실행

1. 왼쪽에서 **travel** 데이터베이스 선택.
2. **쿼리** 탭 열기.
3. 아래 SQL 붙여넣기:
   ```sql
   USE travel;

   ALTER TABLE place_photo
   ADD COLUMN image_path VARCHAR(500) NULL
   COMMENT '국내 여행로그 데이터 기준 상대 경로'
   AFTER image_data;
   ```
4. **F9** 또는 실행 버튼으로 실행.
5. 하단에 오류 없이 실행되면 성공.

### 2-3. 확인

- 왼쪽에서 **place_photo** 테이블 선택 → **구조** 탭에서 **image_path** 컬럼이 있는지 확인.

---

## 3. 명령줄(CLI)로 추가하기

### 3-1. mysql 클라이언트가 있는 경우

- PowerShell 또는 CMD에서:
  ```powershell
  mysql -h 127.0.0.1 -P 3306 -u root -p1234 travel -e "ALTER TABLE place_photo ADD COLUMN image_path VARCHAR(500) NULL COMMENT '국내 여행로그 데이터 기준 상대 경로' AFTER image_data;"
  ```
- 비밀번호를 프롬프트로 넣으려면 `-p` 만 쓰고, 다음 줄에 `1234` 입력.

### 3-2. Docker만 있는 경우 (mysql 클라이언트 없음)

- 컨테이너 안에서 mysql 실행:
  ```powershell
  docker exec -it travel-mariadb mysql -u root -p1234 travel -e "ALTER TABLE place_photo ADD COLUMN image_path VARCHAR(500) NULL COMMENT '국내 여행로그 데이터 기준 상대 경로' AFTER image_data;"
  ```
- 성공하면 아무 메시지 없이 프롬프트로 돌아옵니다.

### 3-3. 확인

- 같은 방식으로 컬럼 목록 조회:
  ```powershell
  docker exec -it travel-mariadb mysql -u root -p1234 travel -e "SHOW COLUMNS FROM place_photo LIKE 'image_path';"
  ```
- 결과에 `image_path` 한 줄이 나오면 추가된 것입니다.

---

## 4. 실행할 SQL 정리 (복사용)

파일 위치: `docs/sql/alter-place_photo-add-image_path.sql`

```sql
USE travel;

ALTER TABLE place_photo
ADD COLUMN image_path VARCHAR(500) NULL
COMMENT '국내 여행로그 데이터 기준 상대 경로'
AFTER image_data;
```

- **USE travel;**  
  - `travel` DB를 사용한다는 뜻.  
  - DBeaver/HeidiSQL에서 이미 `travel`을 선택한 상태라면 **USE travel;** 없이 아래 `ALTER TABLE` 만 실행해도 됩니다.
- **AFTER image_data**  
  - `image_path` 컬럼을 `image_data` 컬럼 바로 뒤에 넣으라는 의미입니다.  
  - 없어도 동작은 하지만, 구조 보기 편하게 하려고 넣은 것입니다.

---

## 5. 자주 나오는 오류와 대처

### "Duplicate column name 'image_path'"

- **의미**: 이미 `image_path` 컬럼이 있습니다.
- **대처**: 추가 작업 불필요. 그대로 **데이터 넣기(INSERT)** 단계로 가면 됩니다.

### "Unknown database 'travel'"

- **의미**: `travel` DB가 없음 (테이블을 아직 안 만든 경우).
- **대처**:  
  1. 먼저 `docs/sql/create-place-tables.sql` **전체**를 실행해서 `travel` DB와 `place`, `place_photo` 테이블을 생성합니다.  
  2. 새로 만든 테이블에는 이미 `image_path`가 포함되어 있으므로, **이 가이드의 ALTER는 실행하지 않아도 됩니다.**

### "Can't connect to MySQL server" / "Connection refused"

- **의미**: 3306 포트로 접속이 안 됨.
- **대처**:  
  - `docker ps` 로 `travel-mariadb`(또는 mariadb) 가 실행 중인지 확인.  
  - Port를 **3306**으로 했는지 다시 확인.

### "Access denied for user 'root'@'...'"

- **의미**: 비밀번호가 다름.
- **대처**: 비밀번호 `1234` 로 설정했는지 확인.  
  - 도커 run 시 `-e MARIADB_ROOT_PASSWORD=1234` 로 만들었는지 확인.

---

## 6. 이 단계 다음에 할 일

- `image_path` 추가까지 끝났으면:
  1. **데이터 넣기**: `backend-fastapi` 폴더에서  
     `$env:MARIADB_PORT="3306"; $env:STORE_IMAGES="0"; python scripts/insert_place_data.py`
  2. **FastAPI**: `.env` 에 `MARIADB_HOST=localhost`, `MARIADB_PORT=3306` 설정 후 서버 실행.

자세한 내용은 `docs/D드라이브_이전_및_DB_작업_정리.md` 의 **3. 데이터 넣기 (INSERT)**, **4. FastAPI에서 3306 사용** 를 참고하면 됩니다.

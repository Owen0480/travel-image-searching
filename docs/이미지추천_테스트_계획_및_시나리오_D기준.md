# 이미지 추천 — 통합 테스트 체크리스트 · 시나리오 (D드라이브 기준)

> 프로젝트를 **D드라이브**로 옮긴 뒤, **이미지 추천** 기능 기준으로 **통합 테스트(기능·연동) 체크리스트**와 **통합 테스트 시나리오**를 표로 정리한 문서입니다.  
> 형식은 `이미지부분_단위테스트_업무흐름_구조도_복사용.md`, `통합테스트_시나리오.md` 를 참고했고,  
> **현재 코드·DB(3306 도커 MariaDB, image_path, embedding_cache)** 를 반영했습니다.

---

## 전제 조건 (현재 구조 요약)

| 구분 | 내용 |
|------|------|
| 프로젝트 경로 | D드라이브 (예: `D:\travel_pj`) |
| 이미지 추천 화면 | `travel-frontend/public/imageSeaching.html` (정적 HTML) |
| 분석 API | 프론트 → **Spring Boot 8080** `/api/v1/recommend/analyze` → **FastAPI 8000** |
| 이미지 서빙 | FastAPI 8000 `/images/{file_path}` (로컬 또는 국내 여행로그 데이터 photo 폴더) |
| DB | **도커 MariaDB 3306**, DB명 `travel`, 테이블 `place`, `place_photo` |
| DB 볼륨 | `D:\docker\mariadb\travel-data` (데이터 영속) |
| place_photo | `photo_file_nm`, `visit_area_id`, `image_path`(상대경로), `image_data`(선택) |
| 임베딩 | 서버 기동 시 `place_photo` 기준 임베딩 계산 → `embedding_cache/` (features.npy, filenames.json, meta.json) 저장/로드 |
| 장소 매칭 | `visit_area_id|photo_file_nm` 키로 이미지–장소 1:1 매칭, 동일 이미지 중복 시 해수욕장/해변 계열 우선 |
| 가이드 | Top1: Chroma RAG + Gemini / Top2·3: CSV 기반 _generate_short_guide |

---

## 1. 통합 테스트 체크리스트 — 이미지 부분 (담당 구간)

**형식: 모듈 / 항목 / 테스트 내용**

| 모듈 | 항목 | 테스트 내용 |
|------|------|-------------|
| 이미지 검색 페이지 (imageSeaching.html) | 사진 선택·업로드 | 이미지 파일 선택 시 FormData에 file 포함되어 Spring 8080 `/api/v1/recommend/analyze` 로 전송되는지 확인 |
| 이미지 검색 페이지 (imageSeaching.html) | 취향 입력 | 취향 입력란(preferenceInput) 값이 FormData의 preference로 포함되어 API에 전달되는지 확인 |
| 이미지 검색 페이지 (imageSeaching.html) | 로딩 UI | 분석 요청 후 "분석 중입니다..." 표시 및 버튼 비활성화 여부 확인 |
| 이미지 검색 페이지 (imageSeaching.html) | 추천 결과 표시 | API 성공 시 "유사 여행지 N곳 추천" 문구, Top3 카드(장소명·주소·유사도%·가이드·이미지) 렌더링 확인 |
| 이미지 검색 페이지 (imageSeaching.html) | 가이드 구간 표시 | 요약·특징·팁 구분 표시 / `**` 제거 |
| 이미지 검색 페이지 (imageSeaching.html) | 결과 이미지 로드 | 각 카드 이미지가 FastAPI 8000 `/images/{photo_file_nm}` URL로 정상 로드되는지 확인 |
| 이미지 검색 페이지 (imageSeaching.html) | 상세 보기 | 카드 "상세 보기" 클릭 시 장소명으로 네이버 지도 검색 새 창이 열리는지 확인 |
| 이미지 검색 페이지 (imageSeaching.html) | 예외 처리 | 매칭 없음 또는 API 실패 시 안내 메시지·alert 표시 여부 확인 |
| Recommend API (Spring Boot 8080) | 분석 요청 중계 | POST `/api/v1/recommend/analyze` (MultipartFile, preference) 수신 후 FastAPI 8000으로 전달하고 응답을 그대로 반환하는지 확인 |
| Recommend API (FastAPI 8000) | DB 연동 | .env MARIADB_HOST·PORT(3306) 기준으로 place_photo·place 조회 및 merged_df 생성 여부 확인 |
| Recommend API (FastAPI 8000) | 임베딩 캐시 | 서버 기동 시 embedding_cache/ 의 place_photo_count 일치 시 features·filenames 로드, 불일치 시 재계산 후 저장하는지 확인 |
| Recommend API (FastAPI 8000) | image_path 해석 | place_photo.image_path 또는 국내 여행로그 데이터(지역)/Sample/01.원천데이터/photo/ 에서 이미지 파일 로드 가능한지 확인 |
| Recommend API (FastAPI 8000) | 유사도·Top3 | 업로드 이미지 CLIP 인코딩 후 코사인 유사도 계산, 임계값 0.6, 동일 이미지·맛집 필터 적용 후 Top3 반환하는지 확인 |
| Recommend API (FastAPI 8000) | 장소 매칭 | visit_area_id\|photo_file_nm 키로 merged_df에서 장소명·주소 1:1 조회(_get_place_info) 되는지 확인 |
| Recommend API (FastAPI 8000) | Top1 가이드 | Chroma RAG + Gemini로 Top1 장소에 대한 가이드 문장 생성 및 GEMINI 실패 시 fallback 문구 반환 여부 확인 |
| Recommend API (FastAPI 8000) | Top2·3 가이드 | _generate_short_guide로 장소명·주소·유사도 기반 짧은 안내 문구 반환 여부 확인 |
| Recommend API (FastAPI 8000) | 이미지 서빙 | GET `/images/{file_path}` 요청 시 backend-fastapi/images 또는 국내 여행로그 데이터 photo 폴더에서 파일 반환되는지 확인 |
| 데이터·DB | place_photo INSERT | insert_place_data.py 실행 시 image_path 상대경로 저장 및 원천 이미지 경로와 일치하는지 확인 |
| 데이터·DB | 이미지–장소 정합성 | verify_and_fix_place_photo.py로 동일 photo_file_nm 다장소 시 해수욕장/해변 우선 1건 유지(선택 --fix) 동작 확인 |

---

## 2. 통합 테스트 케이스 (상세 표 — 참고)

| 번호 | 테스트 대상 (모듈/기능) | 테스트 유형 | 테스트 내용 | 입력/조건 | 예상 결과 | 비고 |
|------|--------------------------|-------------|-------------|-----------|-----------|------|
| 1 | 이미지 검색 페이지 (imageSeaching.html) | 기능 | 사진 선택 및 업로드 | 이미지 파일 선택 (Choose Image / Browse Gallery) | 파일 선택 후 FormData에 file 포함, 분석 요청 시 전송 | input type=file, accept=image/* |
| 2 | 이미지 검색 페이지 (imageSeaching.html) | 기능 | 취향 입력란 입력 | 텍스트 입력 (예: 바다, 한적한 곳, 맛집) | 입력값 유지, FormData에 preference 포함 | id=preferenceInput |
| 3 | 이미지 검색 페이지 (imageSeaching.html) | 기능 | FormData로 이미지·취향 전송 | file + preference, POST multipart | Spring 8080 `/api/v1/recommend/analyze` 호출 | fetch, FormData |
| 4 | 이미지 검색 페이지 (imageSeaching.html) | 기능 | 로딩 중 UI | 분석 요청 후 | "분석 중입니다..." 표시, 버튼 비활성화 | id=analyzeLoading |
| 5 | 이미지 검색 페이지 (imageSeaching.html) | 기능 | 추천 결과 카드 표시 | API 성공 응답 (results 배열, count) | "유사 여행지 N곳 추천" 문구, Top3 카드: 장소명·주소·유사도%·가이드(구간별)·이미지 | matchHeading, resultCardsGrid, guide-card-wrap |
| 6 | 이미지 검색 페이지 (imageSeaching.html) | 기능 | 가이드 구간별 표시 | guide 텍스트 (1) 2) 3) 또는 한 줄 | 요약·특징·팁 구분 / `**` 미노출 | formatGuideSections, renderGuideHtml |
| 7 | 이미지 검색 페이지 (imageSeaching.html) | 기능 | 상세 보기 → 네이버 지도 | 카드 "상세 보기" 클릭 | 네이버 지도 검색(장소명) 새 창 오픈 | window.open, map.naver.com |
| 8 | 이미지 검색 페이지 (imageSeaching.html) | 기능 | 결과 이미지 URL | results[].image_file | FastAPI 8000 `/images/{photo_file_nm}` 로 표시 | FASTAPI_IMAGES_BASE |
| 9 | 이미지 검색 페이지 (imageSeaching.html) | 예외 | 매칭 없음 / API 실패 | 유사도 0.6 미만 또는 서버 오류 | 안내 메시지, matchDescription 갱신, alert | ai_analysis, message |
| 10 | Recommend API (Spring Boot) | 기능 | MultipartFile·preference 수신 | POST /api/v1/recommend/analyze (8080) | FastAPI 8000으로 전달, 응답 그대로 반환 | RecommendController, RecommendService, WebClient |
| 11 | Recommend API (FastAPI) | 기능 | DB 연동 (3306) | MARIADB_HOST, MARIADB_PORT=.env | place_photo + place 조회, merged_df 생성 | _load_from_db, LEFT JOIN place |
| 12 | Recommend API (FastAPI) | 기능 | 임베딩 캐시 로드/저장 | 서버 기동 시 place_photo 건수 | embedding_cache/ meta.json place_photo_count 일치 시 features.npy, filenames.json 로드 | _load_embedding_cache, _save_embedding_cache |
| 13 | Recommend API (FastAPI) | 기능 | 이미지 경로 해석 (image_path) | place_photo.image_path 또는 photo_file_nm | TRAVEL_DATA_ROOT + image_path 또는 국내 여행로그 데이터(지역)/Sample/01.원천데이터/photo/ 에서 파일 로드 | image_path 우선, 없으면 4개 지역 폴더 검색 |
| 14 | Recommend API (FastAPI) | 기능 | 이미지 벡터화 및 유사도 | 업로드 이미지 바이트, db_features(db_filenames) | CLIP 인코딩, 코사인 유사도 Top 후보, 임계값 0.6, 동일 이미지·맛집 필터 후 Top3 | SentenceTransformer clip-ViT-B-32, util.cos_sim |
| 15 | Recommend API (FastAPI) | 기능 | 장소 정보 조회 (이미지–장소 1:1) | key = visit_area_id\|photo_file_nm 또는 photo_file_nm | merged_df에서 VISIT_AREA_ID+PHOTO_FILE_NM 또는 PHOTO_FILE_NM으로 장소명·주소 반환 | _get_place_info |
| 16 | Recommend API (FastAPI) | 기능 | Top1 가이드 생성 (RAG + Gemini) | Top1 장소, preference, Chroma retrieved_chunks | Gemini API로 친근한 가이드 문장 생성 (요약·특징·팁) | _generate_travel_guide, Chroma, GEMINI_API_KEY |
| 17 | Recommend API (FastAPI) | 기능 | Top2·3 가이드 생성 | 장소명·주소·score·POI 등 | CSV 기반 짧은 안내 문구 (Gemini 미사용) | _generate_short_guide |
| 18 | Recommend API (FastAPI) | 기능 | 이미지 서빙 /images/{file_path} | GET /images/{photo_file_nm} | backend-fastapi/images 또는 국내 여행로그 데이터 photo 폴더에서 파일 반환 | main.py serve_image, _resolve_image_path |
| 19 | 데이터·DB | 연동 | place_photo INSERT (image_path) | insert_place_data.py, STORE_IMAGES=0 | place_photo에 image_path 상대경로 저장, 이미지 파일은 원천데이터 photo/ 참조 | 국내 여행로그 데이터(지역)/Sample/... |
| 20 | 데이터·DB | 연동 | 이미지–장소 매칭 검증/정리 | verify_and_fix_place_photo.py | 동일 photo_file_nm 다장소 시 해수욕장/해변 우선 1건만 유지 (선택 --fix) | place_photo 중복 정리 |

---

## 3. 통합 테스트 시나리오 (표)

### 사전 조건

| 항목 | 조건 |
|------|------|
| 프로젝트 | D드라이브 (예: `D:\travel_pj`) 에서 작업 |
| MariaDB | 도커 컨테이너 `travel-mariadb` 실행, 포트 3306, 볼륨 `D:\docker\mariadb\travel-data` |
| DB 테이블 | `place`, `place_photo` 생성 완료, `place_photo.image_path` 컬럼 있음 |
| 데이터 | `insert_place_data.py` 로 place·place_photo INSERT 완료 (국내 여행로그 데이터 경로 설정) |
| Spring Boot | 실행 중, 포트 **8080** |
| FastAPI | `uvicorn app.main:app --reload` 포트 **8000** |
| .env (backend-fastapi) | MARIADB_HOST=localhost, MARIADB_PORT=3306, GEMINI_API_KEY 설정(선택, 미설정 시 Top1 가이드 fallback) |
| 이미지 파일 | 국내 여행로그 데이터(지역)/Sample/01.원천데이터/photo/ 또는 backend-fastapi/images 에서 서빙 가능 |

---

### 시나리오 1: 이미지 검색 — 정상 플로우 (업로드 → 추천 → 가이드 · 상세보기)

| 단계 | 조치 | 예상 결과 |
|------|------|-----------|
| 1-1 | 이미지 검색 페이지 접속 (imageSeaching.html 또는 /home 등) | 업로드 영역, 취향 입력란, "Choose Image" / "Browse Gallery" 표시 |
| 1-2 | 취향 입력 (예: 바다, 맛집) | 입력값 유지 |
| 1-3 | 이미지 파일 선택 후 자동 분석 요청 (또는 업로드 트리거) | 로딩 "분석 중입니다..." 표시, 버튼 비활성화 |
| 1-4 | Spring 8080 → FastAPI 8000 호출 | POST /api/v1/recommend/analyze (multipart: file, preference) |
| 1-5 | FastAPI: 3306 DB place_photo·place 조회, 임베딩 캐시 또는 실시간 임베딩 | 유사도 계산, Top3 결과 생성, Top1 Gemini 가이드 |
| 1-6 | 성공 응답 수신 | "유사 여행지 N곳 추천", 상단 첫 번째 가이드(구간별), 카드 3개(장소명·주소·유사도%·가이드·이미지) |
| 1-7 | 카드 이미지 표시 | FastAPI 8000 `/images/{photo_file_nm}` 로 로드 (image_path 또는 photo 폴더) |
| 1-8 | 카드 "상세 보기" 클릭 | 네이버 지도 검색(장소명) 새 창 오픈 |

---

### 시나리오 2: 이미지 검색 — 매칭 없음 / 예외

| 단계 | 조치 | 예상 결과 |
|------|------|-----------|
| 2-1 | 유사도 0.6 초과인 장소가 없음 | success: false, ai_analysis 또는 message 한글 안내, "매칭된 여행지가 없어요" 등 |
| 2-2 | Gemini API 실패(할당량·키 오류 등) | Top1 가이드는 fallback 문구 또는 _generate_short_guide, "[가이드 생성 실패] 할당량 초과" 등 |
| 2-3 | GEMINI_API_KEY 미설정 | Top1 가이드: "가이드를 생성할 수 없습니다. (.env에 GEMINI_API_KEY 설정 후 서버 재시작)" 또는 short_guide |

---

### 시나리오 3: 인프라 · 연동 예외

| 단계 | 조치 | 예상 결과 |
|------|------|-----------|
| 3-1 | Spring Boot(8080) 중지 후 이미지 분석 요청 | 프론트 fetch 실패, alert "서버가 켜져 있는지 확인해주세요! (Spring Boot 8080, FastAPI 8000)" |
| 3-2 | FastAPI(8000) 중지 후 이미지 분석 요청 | Spring 500 또는 타임아웃 → 프론트에 실패 메시지 |
| 3-3 | MariaDB(3306) 중지 후 FastAPI 기동 또는 분석 요청 | DB 로드 실패 시 merged_df 빈 값, 또는 임베딩 계산 시 연결 오류 로그 |
| 3-4 | 이미지 파일 없음 (image_path 경로에 파일 없음) | 해당 place_photo 행은 임베딩에서 제외 또는 로드 실패 로그, 결과 수 감소 |

---

### 시나리오 4: DB · 캐시 반영

| 단계 | 조치 | 예상 결과 |
|------|------|-----------|
| 4-1 | place_photo INSERT 후 첫 FastAPI 기동 | 임베딩 계산 후 embedding_cache/ 저장 (place_photo_count 일치) |
| 4-2 | place_photo 건수 변경 없이 재기동 | embedding_cache 로드, "임베딩 캐시 로드: N개" 로그, 분석 생략 |
| 4-3 | place_photo 증감 후 재기동 | meta.json place_photo_count 불일치 시 캐시 무시, 재계산 후 캐시 저장 |
| 4-4 | verify_and_fix_place_photo.py --fix 실행 후 | 중복 이미지 정리, 재기동 또는 embedding_cache 삭제 후 재기동 시 추천 반영 |

---

## 4. PPT/문서 활용 시 추천 구성

1. **개요** — D드라이브 구조, 이미지 추천 경로(프론트→8080→8000), DB 3306·image_path·embedding_cache 요약  
2. **통합 테스트 체크리스트 (이미지 부분)** — §1 모듈/항목/테스트 내용 표 (담당 구간)  
3. **통합 테스트 시나리오** — §3 사전 조건 표 + 시나리오 1~4 표 (이미지 검색 정상/예외/인프라/DB·캐시)  
4. **참고** — `docs/D드라이브_이전_및_DB_작업_정리.md`, `docs/sql/create-place-tables.sql`

---

*기준: D드라이브 이전 후, 이미지 추천 DB 3306(도커), place_photo.image_path, embedding_cache, Spring 8080, FastAPI 8000 반영.*

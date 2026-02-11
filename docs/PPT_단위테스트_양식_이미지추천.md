# 단위테스트

## 단위 테스트와 통합 테스트를 통한 시스템 안정성 및 성능 검증 결과

> 아래 표는 **이미지 추천 기능** 기준으로, 단위(로직) + 통합(연동) 관점의 핵심 검증 항목을 `모듈 / 항목 / 테스트 내용` 형식으로 정리한 것입니다.  
> (포트/DB 기준: Spring 8080 → FastAPI 8000, MariaDB 3306 도커, `place/place_photo`, `image_path`, `embedding_cache`)

| 모듈 | 항목 | 테스트 내용 |
|------|------|-------------|
| 이미지 검색 페이지 (`imageSeaching.html`) | 업로드 요청 | 이미지 선택 → FormData `file` 포함 → Spring(8080) `/api/v1/recommend/analyze`로 multipart 전송 확인 |
| 이미지 검색 페이지 (`imageSeaching.html`) | 취향(preference) 전달 | 취향 입력값이 FormData `preference`로 포함되어 Spring→FastAPI까지 전달되는지 확인 |
| 이미지 검색 페이지 (`imageSeaching.html`) | 로딩/UX 안정성 | 분석 중 로딩 표시 + 버튼 비활성화, 완료/실패 시 정상 복구(중복 클릭 방지) 확인 |
| 이미지 검색 페이지 (`imageSeaching.html`) | 결과 개수 문구 | 성공 응답 후 “유사 여행지 N곳 추천”이 응답 `count`(또는 results 길이) 기준으로 표시되는지 확인 |
| 이미지 검색 페이지 (`imageSeaching.html`) | 가이드 가독성(구간) | 가이드가 요약/특징/팁 구간으로 나뉘어 보이는지 + `**` 미노출(제거) 확인 |
| 이미지 검색 페이지 (`imageSeaching.html`) | 카드 3개 렌더 | Top3 카드에 장소명/주소/유사도%/가이드/이미지가 모두 표시되는지 확인 |
| 이미지 검색 페이지 (`imageSeaching.html`) | 이미지 로드(8000) | 결과 이미지가 FastAPI(8000) `/images/{photo_file_nm}`에서 정상 로드되는지(404/깨짐 없음) 확인 |
| 이미지 검색 페이지 (`imageSeaching.html`) | 상세 보기 | “상세 보기” 클릭 시 네이버 지도(장소명 검색) 새 창 오픈 확인 |
| 이미지 검색 페이지 (`imageSeaching.html`) | 예외 UI | 매칭 실패(success=false) 또는 서버 오류 시 안내 문구/alert 및 화면 상태 초기화 확인 |
| Spring Boot Recommend API (8080) | 요청 수신/중계 | MultipartFile + preference 수신 → FastAPI(8000) `/api/v1/recommend/analyze`로 전달 → 응답 그대로 반환 확인 |
| FastAPI RecommendService (8000) | DB 연결(3306) | `.env`의 `MARIADB_HOST/PORT=3306`로 도커 MariaDB 연결 후 `place_photo + place` 로드(merged_df 생성) 확인 |
| FastAPI RecommendService (8000) | image_path 로딩 | `place_photo.image_path` 상대경로를 `TRAVEL_DATA_ROOT` 기준으로 해석해 이미지 파일을 로드하는지 확인 (없으면 지역별 photo 폴더 fallback) |
| FastAPI RecommendService (8000) | 임베딩 캐시(성능) | `embedding_cache/`(features.npy/filenames.json/meta.json) 로드 시 재분석 생략되는지, 건수 불일치 시 재계산/재저장되는지 확인 |
| FastAPI RecommendService (8000) | 유사도/Top3 안정성 | CLIP 임베딩 → 코사인 유사도 계산 → 임계값 0.6 적용 → Top3 생성 로직이 예외 없이 동작하는지 확인 |
| FastAPI RecommendService (8000) | 이미지–장소 매칭 | `visit_area_id|photo_file_nm` 키 기반으로 `_get_place_info`가 장소명/주소를 1:1로 찾아 반환하는지 확인 |
| FastAPI RecommendService (8000) | RAG+Gemini(Top1) | Top1에 대해 Chroma retrieval + Gemini 가이드 생성이 수행되는지, 실패 시 fallback 문구가 반환되는지 확인 |
| FastAPI RecommendService (8000) | Top2·3 가이드 | Top2·3은 `_generate_short_guide`(템플릿)로 생성되는지 확인 |
| FastAPI 이미지 서빙 (8000) | `/images/*` 라우트 | `/images/{file}` 요청이 로컬 `backend-fastapi/images` 또는 국내 여행로그 데이터 `photo/`에서 정상 반환되는지 확인 |
| MariaDB(3306) + 적재 스크립트 | 데이터 적재 정합성 | `insert_place_data.py`로 `place/place_photo` 적재 시 `image_path` 저장 및 실제 파일 경로와 대응되는지 확인 |
| 운영 안정성(재기동) | 캐시/재시작 | FastAPI 재시작 시 캐시 로드로 응답 지연이 줄어드는지(최초 1회 계산 vs 이후 로드) 확인 |


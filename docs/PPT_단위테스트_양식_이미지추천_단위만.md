# 단위테스트

## 단위 테스트를 통한 기능 안정성 검증 결과 (Frontend + Backend — 이미지 추천)

> 아래 표는 **외부 연동(네트워크/실DB/도커)** 없이, 이미지 추천 기능의 **프론트(UI 로직)** + **백엔드(FastAPI 내부 로직)** 를 단위(Unit)로 검증하는 항목을 `모듈 / 항목 / 테스트 내용` 형식으로 정리한 것입니다.

| 모듈 | 항목 | 테스트 내용 |
|------|------|-------------|
| 프론트(`imageSeaching.html`) | 파일 선택 트리거 | 버튼 클릭 → 숨김 file input 클릭 트리거되는지(이벤트 연결) 확인 |
| 프론트(`imageSeaching.html`) | preference 읽기 | `preferenceInput` 값이 trim 처리되어 formData에 담길 준비가 되는지 확인(빈 값은 제외) |
| 프론트(`imageSeaching.html`) | 로딩 상태 토글 | 분석 시작 시 로딩 표시/버튼 disabled, 종료 시 로딩 숨김/버튼 enabled로 정상 복구되는지 확인 |
| 프론트(`imageSeaching.html`) | N곳 추천 문구 | 성공 응답 시 `count`(없으면 results.length) 기반으로 “유사 여행지 N곳 추천” 문구가 설정되는지 확인 |
| 프론트(`imageSeaching.html`) | 결과 카드 렌더 | results 3개를 받아 카드 3개 HTML이 생성되고, 장소명/주소/유사도% 영역이 비어있지 않게 들어가는지 확인 |
| 프론트(`imageSeaching.html`) | 이미지 URL 조합 | `FASTAPI_IMAGES_BASE + /images/ + encodeURIComponent(image_file)` 형태로 URL이 생성되는지 확인 |
| 프론트(`imageSeaching.html`) | 가이드 파싱 | `formatGuideSections()`가 `1)`, `2)`, `3)`을 요약/특징/팁 라벨로 분리하는지 확인 |
| 프론트(`imageSeaching.html`) | 가이드 렌더링 | `renderGuideHtml()`이 라벨+본문 구조 HTML을 생성하는지, maxLines만큼만 출력하는지 확인 |
| 프론트(`imageSeaching.html`) | `**` 제거 | 가이드 텍스트에 포함된 `**`가 화면 렌더링 문자열에서 제거되는지 확인 |
| 프론트(`imageSeaching.html`) | 빈 가이드 처리 | guide가 빈 문자열/공백/undefined일 때 placeholder(“유사한 분위기의 여행지입니다.”)가 표시되는지 확인 |
| 프론트(`imageSeaching.html`) | XSS/이스케이프 | `<script>` 등 HTML 특수문자가 실행되지 않고 텍스트로만 렌더링되도록 escape 되는지 확인 |
| 프론트(`imageSeaching.html`) | 실패/예외 UI | success=false 또는 예외 시 matchHeading/설명/카드 영역이 안내 상태로 돌아가는지 확인 |

| FastAPI(`RecommendService`) | Gemini 키 유무 분기 | `GEMINI_API_KEY` 유무에 따라 `gemini_model`이 설정/미설정 되는지 확인 |
| FastAPI(`RecommendService`) | Gemini 에러 사유 변환 | `_gemini_error_reason()`가 429/quota/billing/401 등을 한글 사유로 매핑하는지 확인 |
| FastAPI(`RecommendService`) | 문자열 정리 | `_safe_str()`가 None/NaN/공백을 안전하게 빈 문자열로 변환하는지 확인 |
| FastAPI(`RecommendService`) | 장소 조회(키/파일명) | `_get_place_info()`가 (1) `visit_area_id|photo_file_nm` 키, (2) 파일명 단독 입력 모두에서 올바른 행을 찾는지 확인(테스트용 DataFrame로 검증) |
| FastAPI(`RecommendService`) | 캐시 메타 검증 | `_load_embedding_cache()`가 `place_photo_count` 불일치 시 캐시를 무시(None 반환)하는지 확인 |
| FastAPI(`RecommendService`) | 구버전 캐시 무시 | filenames.json이 “파일명만”인 구버전이면 재계산 유도를 위해 캐시를 무시하는지 확인 |
| FastAPI(`RecommendService`) | 동일 이미지 1개로 정리 | 같은 `image_file`이 여러 후보로 들어올 때 이미지별 1개만 남기는지 확인 |
| FastAPI(`RecommendService`) | 해변 검색 시 맛집 제외 | 후보에 해수욕장/해변 계열이 다수일 때 피자/맛집/식당 계열이 제외되는지 확인(가짜 place_name 리스트로 검증) |
| FastAPI(`main.py`) | 이미지 경로 안전성 | `_resolve_image_path()`가 `..` 등 path traversal 입력을 거부(None)하는지 확인 |


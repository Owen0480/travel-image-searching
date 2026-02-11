# 단위 테스트

> 이미지 추천 기능(프론트 + 백엔드) 단위 테스트 케이스 표.  
> **수행결과**: 백엔드 No 16·17·18·19·24는 스크립트(`backend-fastapi/scripts/run_unit_tests.py`)로 검증 후 Pass 반영. 프론트(No 1~14) 및 No 15·20~23은 브라우저/서비스 환경에서 수행 필요.

| No | 케이스명 | 메뉴경로 | 케이스 내용 | 사전조건 | 테스트데이터 | 예상결과 | 수행결과 |
|----|----------|----------|-------------|----------|--------------|----------|----------|
| 1 | 이미지 파일 선택 트리거 | 이미지추천 > 이미지검색 | "Choose Image/Browse Gallery" 버튼 클릭 시 파일 선택창이 열리는지 확인 | 브라우저에서 페이지 로드 완료 | - | OS 파일 선택창이 열림 | 수행 필요 |
| 2 | preference trim 처리 | 이미지추천 > 이미지검색 | 취향 입력란 앞뒤 공백이 전송 시 trim 되는지 확인 | 페이지 로드 완료 | preference: `"  바다  "` | 서버로 보내는 값이 `"바다"`로 처리됨 | 수행 필요 |
| 3 | preference 빈 값 제외 | 이미지추천 > 이미지검색 | 취향이 비어 있을 때 FormData에 preference 미포함 여부 확인 | 페이지 로드 완료 | preference: `""` | FormData에 preference 없어도 요청 정상 처리 | 수행 필요 |
| 4 | 로딩 UI 표시/복구 | 이미지추천 > 이미지검색 | 분석 시작 시 로딩 표시·버튼 비활성화, 종료 시 해제되는지 확인 | 이미지 선택 가능 상태 | 임의 이미지 1장 | 로딩 표시 후 응답 시 로딩 숨김·버튼 활성화 | 수행 필요 |
| 5 | N곳 추천 문구 | 이미지추천 > 이미지검색 | 성공 응답 시 "유사 여행지 N곳 추천" 문구가 count 기준으로 표시되는지 확인 | API success=true 반환 | count=3 | 상단 제목이 "유사 여행지 3곳 추천"으로 표시 | 수행 필요 |
| 6 | 결과 카드 3개 렌더 | 이미지추천 > 이미지검색 | results 3개 수신 시 카드 3개가 생성·표시되는지 확인 | API가 results 3개 반환 | place_name/address/score/image_file/guide 포함 | 카드 3개, 장소명·주소·매칭% 표시 | 수행 필요 |
| 7 | 이미지 URL 인코딩 | 이미지추천 > 이미지검색 | image_file에 특수문자 포함 시 URL이 encodeURIComponent로 생성되는지 확인 | results 반환 | image_file: `"a b(1).jpg"` | URL에 인코딩 적용되어 로드 시도됨 | 수행 필요 |
| 8 | 가이드 구간 파싱 | 이미지추천 > 이미지검색 | 1) 2) 3) 형식 가이드가 요약/특징/팁으로 구간 분리·표시되는지 확인 | success 응답 | guide: `1) 요약\n2) 특징\n3) 팁` | 요약·특징·팁 라벨과 함께 줄 단위 표시 | 수행 필요 |
| 9 | 가이드 maxLines 제한 | 이미지추천 > 이미지검색 | 가이드 구간이 많을 때 maxLines(4)까지만 표시되는지 확인 | success 응답 | guide 5줄 이상 | 카드 가이드가 4구간까지만 표시 | 수행 필요 |
| 10 | ** 제거 | 이미지추천 > 이미지검색 | 가이드 내 **가 화면에서 제거되어 보이는지 확인 | success 응답 | guide: `1) **요약** 입니다` | 화면에 `**` 미노출 | 수행 필요 |
| 11 | 빈 guide placeholder | 이미지추천 > 이미지검색 | guide가 빈 문자열/누락일 때 placeholder 문구가 표시되는지 확인 | success 응답(guide 없음) | guide: `""` 또는 undefined | "유사한 분위기의 여행지입니다." 표시 | 수행 필요 |
| 12 | XSS/이스케이프 | 이미지추천 > 이미지검색 | guide 등에 HTML/스크립트가 들어와도 텍스트로만 표시되는지 확인 | success 응답(가짜 데이터) | guide: `"<script>alert(1)</script>"` | 스크립트 미실행, escape 처리 | 수행 필요 |
| 13 | 실패 응답 UI | 이미지추천 > 이미지검색 | success=false 또는 오류 시 제목/설명/카드가 안내 상태로 복구되는지 확인 | API 실패 응답 | {success:false, ai_analysis:'...'} | 제목·설명 초기화, 카드 placeholder | 수행 필요 |
| 14 | 상세 보기(지도) | 이미지추천 > 이미지검색 | "상세 보기" 클릭 시 네이버 지도 검색 새 창이 열리는지 확인 | results place_name 존재 | place_name: "이호테우해수욕장" | 네이버 지도 검색 URL 새 창 오픈 | 수행 필요 |
| 15 | Gemini 키 유무 분기 | 백엔드 > RecommendService | GEMINI_API_KEY 유무에 따라 gemini_model 설정/미설정 되는지 확인 | FastAPI 서비스 초기화 | .env KEY 있음/없음 | KEY 있으면 모델 설정, 없으면 None | 수행 필요 |
| 16 | Gemini 에러 사유 변환 | 백엔드 > RecommendService | _gemini_error_reason()이 429/quota/billing/401 등을 한글 사유로 매핑하는지 확인 | recommend_service 로드됨 | 예외 메시지(429, quota 등) | 한글 안내 문구 반환 | **Pass** |
| 17 | _safe_str() | 백엔드 > RecommendService | None/NaN/공백이 빈 문자열로 안전 변환되는지 확인 | 테스트용 DataFrame 또는 값 주입 | None, NaN, "  " | 빈 문자열 또는 trim된 문자열 | **Pass** |
| 18 | _get_place_info(키) | 백엔드 > RecommendService | visit_area_id\|photo_file_nm 키로 merged_df에서 올바른 행을 찾는지 확인 | merged_df 준비(또는 mock) | key: "B_123\|a.jpg" | 해당 visit_area_id·photo_file_nm의 장소명·주소 반환 | **Pass** |
| 19 | _get_place_info(파일명) | 백엔드 > RecommendService | 파일명 단독 입력 시에도 행을 찾는지 확인(구버전 호환) | merged_df 준비(또는 mock) | file_name: "a.jpg" | PHOTO_FILE_NM 매칭 행의 장소명·주소 반환 | **Pass** |
| 20 | 캐시 메타 검증 | 백엔드 > RecommendService | _load_embedding_cache()가 place_photo_count 불일치 시 캐시 무시하는지 확인 | embedding_cache/ 존재 | meta.json count ≠ DB count | None 반환(캐시 미사용) | 수행 필요 |
| 21 | 구버전 캐시 무시 | 백엔드 > RecommendService | filenames.json이 파일명만(구버전)일 때 캐시 무시·재계산 유도되는지 확인 | filenames 형식 확인 | filenames에 "|" 없음 | 캐시 무시, None 반환 | 수행 필요 |
| 22 | 동일 이미지 1건 유지 | 백엔드 > RecommendService | 같은 image_file이 여러 후보로 들어올 때 이미지별 1개만 남기는지 확인 | analyze_image 호출(또는 유사도 결과 mock) | 동일 파일명 다수 후보 | 결과에 동일 image_file 1건만 포함 | 수행 필요 |
| 23 | 해변 검색 시 맛집 제외 | 백엔드 > RecommendService | 후보에 해수욕장/해변 다수일 때 피자/맛집/식당 계열이 제외되는지 확인 | raw_results에 맛집·해수욕장 혼재 | place_name: "도우개러지", "이호테우해수욕장" 등 | 맛집 계열 제외, 해변 계열만 유지 | 수행 필요 |
| 24 | 이미지 경로 안전성 | 백엔드 > main.py | _resolve_image_path()가 ".." 등 path traversal 입력 시 None 반환하는지 확인 | main 모듈 로드 | file_path: "../../etc/passwd" | None 반환 | **Pass** |

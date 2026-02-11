# GPT에게 이미지 업로드 부분 만들어 달라고 할 때 쓰는 설명문

아래 통째로 복사해서 GPT에게 붙여넣으면 된다.

---

다음 조건으로 **이미지 업로드·유사 여행지 추천** 구간만 만들어줘.

## 프로젝트 개요 (이미지 부분만)
- **역할**: 사용자가 사진을 올리면, 비슷한 분위기/색감의 여행지 사진을 추천해 주는 기능이다.
- **흐름**: 사용자 → 이미지 검색 페이지에서 사진 선택 + 취향(예: 가족, 맛집) 입력 → "매칭 여행지 찾기" 클릭 → **Spring Boot**가 이미지·취향을 받아서 **FastAPI**로 넘김 → FastAPI에서 **CLIP**으로 이미지를 벡터로 바꾸고, 미리 벡터화해 둔 기준 이미지(여행지 사진 DB)와 **코사인 유사도**로 비슷한 것 Top3 뽑음(임계값 0.6) → **ChromaDB**로 장소 문서 RAG 검색 후 **Gemini API**로 가이드 문장 생성 → 결과를 Spring 경유로 프론트에 전달 → **결과 카드**(장소명, 유사도%, 가이드, 이미지) 표시 → "상세 보기" 클릭 시 **네이버 지도** 검색 새 창 오픈.
- **기술 스택 (이미지 구간만)**  
  - 프론트: React, Vite, Axios (FormData multipart 전송, JWT), 결과 카드 UI, 네이버 지도 링크  
  - 백엔드: Spring Boot, Java (RecommendController, MultipartFile, WebClient로 FastAPI 호출)  
  - AI: FastAPI, Python, CLIP(sentence-transformers), ChromaDB, Gemini API  
  - 데이터: images 폴더(기준 여행지 사진), tour.csv·place.csv(장소–이미지 매핑, 장소명·주소 등)  
  - 외부: 네이버 지도(상세 보기용 URL)

## 만들어 달라는 것
1. **단위 테스트 계획서**  
   위 이미지 업로드 흐름만 보고, **표 형태**로 만들어줘.  
   컬럼 예: 번호, 테스트 대상(모듈/기능), 테스트 유형, 테스트 내용, 입력/조건, 예상 결과, 비고.  
   프론트(사진 선택, 취향 입력, FormData 전송, 로딩, 결과 카드, 상세 보기, 에러 처리), Spring(Recommend API, Multipart 수신, WebClient), FastAPI(CLIP, 유사도, 장소 정보, ChromaDB, Gemini), 데이터(images, CSV), 네이버 지도 연동까지 포함해서 항목 나눠줘.

2. **전체 업무흐름도 (Mermaid)**  
   이미지 업로드부터 결과 표시·네이버 지도까지의 **업무 흐름**을 Mermaid flowchart로 그려줘.  
   사용자/프론트 → Spring Boot → FastAPI → 데이터(images, CSV) → 결과 → 네이버 지도 흐름이 보이게.

3. **시스템 구조도 (기술 스택 활용, Mermaid)**  
   이미지 구간만 보이는 **시스템 구조도**를 Mermaid flowchart로 그려줘.  
   프론트엔드(React, Vite, Axios, FormData, 결과 카드, 네이버 지도), 메인 백엔드(Spring Boot, RecommendController, WebClient), AI 서비스(FastAPI, CLIP, 유사도, ChromaDB, Gemini), 데이터(images 폴더, tour·place csv), 네이버 지도가 레이어/노드로 구분되게.

---

위 세 가지(표 + 업무흐름도 Mermaid + 시스템 구조도 Mermaid)를 **이미지 업로드·유사 여행지 추천** 구간만 대상으로 만들어줘.

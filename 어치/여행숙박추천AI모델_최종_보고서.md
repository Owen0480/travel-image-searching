# 여행 & 숙박 추천 AI 모델

**이미지·취향 기반 대화형 인터페이스를 통한 여행지 추천 시스템**

Team 3 | 이주영, 김건호, 김재영, 김성미

---

## 목차

1. 프로젝트 개요  
2. 팀 구성 및 개발 계획  
3. 시스템 설계  

---

## 1. 프로젝트 개요 (Project Overview)

### 추진 배경 및 목적

- 사용자가 올린 **사진**과 **취향(가족/맛집/힐링 등)**을 입력받아, 유사한 여행지를 추천하고 AI 가이드를 제공하는 서비스
- **이미지 유사도(CLIP)** + **RAG(Chroma + Gemini)** 기반으로, 근거 있는 설명과 취향에 맞는 문장을 생성
- 채팅 기반 여행 상담, 여행 스타일 분석(팝업) 등으로 사용자 경험 확장

### 주요 기능 (실제 구현 기준)

| 구분 | 기능 | 설명 |
|------|------|------|
| 인증 | Google OAuth 로그인 | OAuth2.0 기반 회원가입·로그인 |
| 인증 | JWT + Refresh Token | Access Token 갱신, Redis 저장 |
| 이미지 추천 | 이미지 기반 여행지 추천 | 사진 업로드 → CLIP 유사도 → Top3 장소 |
| 이미지 추천 | 취향 입력 + RAG 가이드 | 취향(가족/맛집 등)에 맞춰 Chroma retrieval 후 Gemini로 가이드 생성 |
| 이미지 추천 | 상세 보기 | 추천 장소 네이버 지도 검색 연결 |
| 채팅 | 여행 채팅 | FastAPI travel-chat 연동, 대화 이력 |
| 대화 | 대화 저장·조회 | Conversation ID 기반 저장·목록 (SavedConversation) |
| 분석 | 여행 스타일 분석 | 관심사 선택 → AI 여행 타입 분석 (팝업) |
| 마이페이지 | 마이페이지 | 사용자 정보, (필요 시 프로필 등) |
| 기타 | 네비게이션 | 채팅, 이미지 검색, 마이페이지, 여행 스타일 등 라우팅 |

---

## 2. 팀 구성 및 개발 계획 (Team & Development Plan)

### 팀원 역할 및 기능 분담

- **이주영, 김건호, 김재영, 김성미** — 역할 분담은 팀 내부 기준으로 정리
- **주요 담당 영역 예시**: 프론트(React·이미지 검색·UI), 백엔드(Spring Boot·FastAPI), 이미지 추천·RAG·채팅·여행 스타일 등

### 개발 환경 및 기술 스택 (실제 프로젝트 기준)

#### Frontend

| 항목 | 설명 | 비고 |
|------|------|------|
| React | SPA 라이브러리 | 18.x |
| Vite | 빌드·개발 서버 | 5.x |
| React Router | 라우팅 | 6.x |
| Tailwind CSS | 유틸리티 CSS | CDN / 설정 |
| Axios | HTTP 클라이언트 | JWT·Refresh 연동 |

#### Backend

| 항목 | 설명 | 비고 |
|------|------|------|
| Spring Boot | 애플리케이션 프레임워크 | 3.x, port 8081 |
| Java | 비즈니스 로직 | JDK 17 |
| Gradle | 빌드 도구 | 8.x |
| FastAPI | Python API 서버 | port 8000, 이미지 추천·채팅·여행 스타일 |

#### AI / 추천

| 항목 | 설명 | 비고 |
|------|------|------|
| CLIP (sentence-transformers) | 이미지 유사도 | clip-ViT-B-32 |
| ChromaDB | 벡터 DB (RAG) | 장소 문서 청크 저장·검색 |
| sentence-transformers | 텍스트 임베딩 | all-MiniLM-L6-v2 |
| Google Gemini | 가이드 생성·채팅 등 | gemini-2.5-flash |
| LangGraph / LangChain | (그래프·채팅 등) | FastAPI 내 활용 |

#### Database & Infra

| 항목 | 설명 | 비고 |
|------|------|------|
| MariaDB | RDBMS | 회원·대화·여행 스타일 결과 등 |
| Redis | 세션·토큰 | Refresh Token 등 |
| Git | 형상 관리 | - |

---

## 3. 시스템 설계 (System Design)

### 프로젝트 구조 (요약)

- **travel-frontend**: React(Vite), 라우트: 로그인, 채팅, 이미지 검색, 여행 스타일, 마이페이지
- **travel-springboot**: 인증(OAuth·JWT), recommend 프록시(→ FastAPI), 채팅·대화·여행 스타일 API
- **backend-fastapi**: 이미지 추천(CLIP·Chroma·Gemini), travel-chat, travel-style 분석, 정적 이미지 서빙(/images)

### 기능 정의서 (실제 구현 기준)

| 번호 | Depth1 | Depth2 | 기능명 | 기능 설명 |
|------|--------|--------|--------|-----------|
| 001 | 메인 | 인증 | OAuth 로그인 | Google OAuth2.0 기반 로그인·회원가입 |
| 002 | 메인 | 인증 | JWT·Refresh | Access/Refresh Token 발급·갱신, Redis 저장 |
| 003 | 메인 | 이미지 추천 | 이미지 업로드·추천 | 사진 업로드 → Spring → FastAPI /recommend/analyze → Top3 장소 |
| 004 | 메인 | 이미지 추천 | 취향 입력·RAG 가이드 | 취향 텍스트 + Chroma retrieval → Gemini 취향 반영 가이드 |
| 005 | 메인 | 이미지 추천 | 상세 보기 | 추천 장소명·주소로 네이버 지도 검색 |
| 006 | 메인 | 채팅 | 여행 채팅 | FastAPI /travel-chat 연동, 대화 이력 표시 |
| 007 | 메인 | 대화 | 대화 세션·저장 | Conversation ID 생성·저장·목록 조회 |
| 008 | 메인 | 분석 | 여행 스타일 분석 | 관심사 선택 → FastAPI 분석 → 팝업 결과 |
| 009 | 마이페이지 | 회원 | 마이페이지 | 사용자 정보 조회 등 |
| 010 | 공통 | 보안 | recommend 비인증 허용 | /api/v1/recommend/** permitAll (이미지 검색용) |

### 주요 업무 흐름 (요약)

1. **로그인**: 로그인 화면 → OAuth 또는 이메일/비밀번호 → JWT 발급 → 메인(채팅 등) 이동  
2. **이미지 추천**: 이미지 검색 화면 → 사진·취향 입력 → Spring 8081 → FastAPI 8000 → CLIP 유사도 + Chroma RAG + Gemini → Top3 결과·가이드 표시 → 상세 보기(지도)  
3. **채팅**: 채팅 화면 → 메시지 전송 → Spring → FastAPI travel-chat → 응답 표시  
4. **여행 스타일**: 여행 스타일 화면 → 관심사 선택 → FastAPI 분석 → 팝업 결과  

### 화면 구성 (실제 라우트 기준)

| 화면 | 경로 | 설명 |
|------|------|------|
| 로그인 | /login | OAuth·이메일 로그인 |
| 회원가입 | /register | 회원가입 |
| 채팅 | /chat | 여행 채팅 |
| 이미지 검색 | /image-search | 사진·취향 입력, Top3 추천·가이드 |
| 여행 스타일 | /travel-style | 관심사 선택, AI 타입 분석(팝업) |
| 마이페이지 | /mypage | 사용자 정보 |

### 데이터베이스 설계 (ERD)

- 실제 테이블은 Spring Boot 엔티티 기준 (User, RefreshToken, SavedConversation, TravelStyleResult 등)
- 상세 ERD는 프로젝트 내 ERD 다이어그램 또는 엔티티 정의 참고

---

## 4. 기타

- **보고서 작성일**: 2026년 기준, 실제 구현(코드베이스)에 맞춰 정리
- **원본 문서**: 여행숙박추천AI모델_최종에 최종.txt (발표/기획 초안)를 바탕으로, 구현된 기능·기술 스택으로 수정·보완

**경청해 주셔서 감사합니다. 질문 주시면 답변 드리겠습니다.**

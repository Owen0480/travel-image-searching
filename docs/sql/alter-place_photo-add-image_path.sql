-- 기존 place_photo 테이블에 image_path 컬럼 추가 (국내 여행로그 데이터 경로 저장용)
-- 실행: travel DB 선택 후 실행. 이미 컬럼이 있으면 에러 나므로 무시하거나 조건부 실행.

USE travel;

-- MariaDB 10.5.2+ / MySQL 8.0.12+ 에서 IF NOT EXISTS 지원
ALTER TABLE place_photo ADD COLUMN image_path VARCHAR(500) NULL COMMENT '국내 여행로그 데이터 기준 상대 경로' AFTER image_data;

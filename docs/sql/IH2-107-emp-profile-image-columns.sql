-- =========================================================
-- IH2-107 직원 프로필 이미지 — EMPLOYEE 테이블 컬럼 추가
-- SQLGate로 직접 실행/커밋 예정 (이 저장소 코드로는 반영되지 않음 — 기록 목적)
-- SeaweedFS에 저장된 프로필 이미지의 URL/fid를 EMPLOYEE에 보관
--   - PROFILE_IMAGE_URL: 화면에 바로 뿌릴 수 있는 조회용 URL
--   - PROFILE_IMAGE_FID : SeaweedFS 파일 식별자(fid). 교체/삭제 시 이 값으로 SeaweedFS에 삭제 요청
-- =========================================================

-- 이전에 만들어뒀던 미사용 컬럼(IMAGE_URL) 제거 — EmpEntity에 매핑된 적 없음
ALTER TABLE EMPLOYEE DROP COLUMN IMAGE_URL;

ALTER TABLE EMPLOYEE ADD (
    PROFILE_IMAGE_URL VARCHAR2(500),
    PROFILE_IMAGE_FID VARCHAR2(100)
);

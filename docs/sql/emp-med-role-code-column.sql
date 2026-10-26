-- =========================================================
-- 직원 의료진 역할(MED_ROLE_CD) — EMPLOYEE 테이블 컬럼 추가
-- SQLGate로 직접 실행 예정 (이 저장소 코드로는 반영되지 않음 — 기록 목적)
--   - MED_ROLE_CODE : 공통코드 MED_ROLE_CD 그룹의 codeValue를 그대로 저장 (예: "05")
-- =========================================================

ALTER TABLE EMPLOYEE ADD (
    MED_ROLE_CODE VARCHAR2(10)
);

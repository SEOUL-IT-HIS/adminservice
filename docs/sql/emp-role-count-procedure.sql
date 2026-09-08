-- =========================================================
-- ROLE_ID로 EMP_ROLE 건수를 세는 프로시저
-- (해당 역할이 배정된 직원이 몇 명인지)
-- SQLGate로 직접 실행 (이 저장소 코드로는 반영되지 않음 — 기록 목적)
-- =========================================================

CREATE OR REPLACE PROCEDURE ADMIN.PROC_COUNT_EMP_BY_ROLE (
    p_role_id IN  ADMIN.EMP_ROLE.ROLE_ID%TYPE,
    p_count   OUT NUMBER
) AS
BEGIN
    SELECT COUNT(*)
      INTO p_count
      FROM ADMIN.EMP_ROLE
     WHERE ROLE_ID = p_role_id;
END PROC_COUNT_EMP_BY_ROLE;
/

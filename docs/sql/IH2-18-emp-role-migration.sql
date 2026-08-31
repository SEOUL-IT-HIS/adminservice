-- =========================================================
-- IH2-18 직원 역할 — 공통코드 방식 → ROLE/EMP_ROLE 테이블 방식으로 일원화
-- SQLGate로 직접 실행 예정 (이 저장소 코드로는 반영되지 않음 — 기록 목적)
--
-- 그전까지 역할이 두 갈래로 갈라져 있었다.
--   A) COMMON_CODE(ROLE_CD) → EMPLOYEE.MED_ROLE_CODE 문자열 복사 — 실제로 쓰이던 쪽
--   B) ROLE → EMP_ROLE → EMPLOYEE — 테이블/FK/UNIQUE는 살아있는데 자바 코드가 없어 죽어 있던 쪽
-- B로 일원화하고 A(MED_ROLE_CODE 컬럼)는 삭제했다.
--
-- 아래 1~5를 순서대로 실행할 것. 5번(컬럼 DROP)은 되돌릴 수 없으므로
-- 반드시 애플리케이션 코드에서 MED_ROLE_CODE 참조를 먼저 제거하고 배포한 뒤에 실행한다.
-- =========================================================


-- ---------------------------------------------------------
-- 1. ROLE 에 없던 역할 2건 추가
--    COMMON_CODE(ROLE_CD)에는 11 전공의 / 12 전문의가 있는데 ROLE 에는 01~10 뿐이었다.
--    이대로 이관하면 전공의 3명 / 전문의 1명이 역할을 못 받는다.
--    ROLE_ID 는 자동 채번이 없어 직접 넣는다.
-- ---------------------------------------------------------
INSERT INTO ROLE (ROLE_ID, ROLE_CODE, ROLE_NAME, DESCRIPTION, USE_YN)
VALUES ('7a1c9e40-3b52-4d18-9f60-1c8a5e2b7d31', '11', '전공의', NULL, 'Y');

INSERT INTO ROLE (ROLE_ID, ROLE_CODE, ROLE_NAME, DESCRIPTION, USE_YN)
VALUES ('7a1c9e40-3b52-4d18-9f60-1c8a5e2b7d32', '12', '전문의', NULL, 'Y');

COMMIT;


-- ---------------------------------------------------------
-- 2. EMPLOYEE.MED_ROLE_CODE → EMP_ROLE 이관
--    EMP_ROLE_ID 는 자동 채번이 없어 SYS_GUID() 를 기존 행과 같은 UUID 형태로 만들어 넣는다.
--    ASSIGNED_AT 은 기본값 SYSTIMESTAMP 라 생략한다.
--    NOT EXISTS  : 이미 배정된 조합은 건너뛴다 (UK_EMP_ROLE 중복 방지, 기존 ASSIGNED_AT 보존).
--    마지막 NOT  : admin 은 MED_ROLE_CODE 가 10(기타)인데 EMP_ROLE 에는 이미
--                  01(시스템관리자)이 들어있어 값이 어긋나 있었다. 10 은 쓰레기값으로 보고 제외한다.
-- ---------------------------------------------------------
INSERT INTO EMP_ROLE (EMP_ROLE_ID, EMP_ID, ROLE_ID, ASSIGNED_BY)
SELECT LOWER(REGEXP_REPLACE(RAWTOHEX(SYS_GUID()),
              '(.{8})(.{4})(.{4})(.{4})(.{12})', '\1-\2-\3-\4-\5')),
       e.EMP_ID,
       r.ROLE_ID,
       'f8f38c59-d0ed-4882-9665-7873675ad020'
FROM EMPLOYEE e
JOIN ROLE r ON r.ROLE_CODE = e.MED_ROLE_CODE
WHERE NOT EXISTS (
        SELECT 1 FROM EMP_ROLE er
        WHERE er.EMP_ID = e.EMP_ID AND er.ROLE_ID = r.ROLE_ID)
  AND NOT (e.EMP_ID = 'f8f38c59-d0ed-4882-9665-7873675ad020'
           AND r.ROLE_CODE = '10');

COMMIT;


-- ---------------------------------------------------------
-- 3. admin 의 중복 역할 정리
--    EMP_ROLE 의 UNIQUE 가 (EMP_ID, ROLE_ID) 조합이라 1인 다역이 가능하지만,
--    화면은 드롭다운(1인 1역)으로 가기로 했다. admin 만 01+02 두 개를 갖고 있었고,
--    roleIds[0] 을 쓰는 화면에서 어느 쪽이 보일지 정해지지 않으므로 01 만 남긴다.
--    (02 개인정보보호책임자는 ROLE 에 그대로 남아 나중에 다시 배정할 수 있다.)
-- ---------------------------------------------------------
DELETE FROM EMP_ROLE
WHERE EMP_ID = 'f8f38c59-d0ed-4882-9665-7873675ad020'
  AND ROLE_ID = (SELECT ROLE_ID FROM ROLE WHERE ROLE_CODE = '02');

COMMIT;


-- ---------------------------------------------------------
-- 4. 컬럼 삭제 전 백업
--    위 2번의 admin 사례처럼 EMP_ROLE 과 값이 어긋난 행이 있어서
--    EMP_ROLE 만으로는 원래 MED_ROLE_CODE 를 복원할 수 없다.
-- ---------------------------------------------------------
CREATE TABLE EMPLOYEE_MED_ROLE_BAK AS
SELECT EMP_ID, EMP_NO, MED_ROLE_CODE FROM EMPLOYEE;


-- ---------------------------------------------------------
-- 5. MED_ROLE_CODE 컬럼 삭제 (되돌릴 수 없음)
--    애플리케이션에서 참조를 모두 제거한 뒤 실행할 것.
--    EmpEntity 에 @Column(name = "MED_ROLE_CODE") 가 남아있는 상태로 실행하면 기동 시 깨진다.
--    Oracle DDL 은 자동 커밋이라 COMMIT 이 필요 없다.
-- ---------------------------------------------------------
ALTER TABLE EMPLOYEE DROP COLUMN MED_ROLE_CODE;


-- ---------------------------------------------------------
-- 검증
-- ---------------------------------------------------------
-- 역할 마스터 12건 (01~12)
SELECT ROLE_CODE, ROLE_NAME FROM ROLE ORDER BY ROLE_CODE;

-- 직원 수 = EMP_ROLE 행수 = 역할 보유 직원 수 (전원 1인 1역)
SELECT (SELECT COUNT(*) FROM EMPLOYEE)                  AS 직원,
       (SELECT COUNT(*) FROM EMP_ROLE)                  AS 역할배정,
       (SELECT COUNT(DISTINCT EMP_ID) FROM EMP_ROLE)    AS 역할보유직원
FROM DUAL;

-- 역할이 2개 이상인 직원 (0건이어야 정상 — EMP_ID 로 묶을 것, 동명이인이 있어 NAME 으로 묶으면 안 된다)
SELECT e.EMP_NO, e.NAME, COUNT(*)
FROM EMP_ROLE er JOIN EMPLOYEE e ON e.EMP_ID = er.EMP_ID
GROUP BY e.EMP_ID, e.EMP_NO, e.NAME HAVING COUNT(*) > 1;

-- 컬럼이 지워졌는지 (0건이어야 정상)
SELECT COLUMN_NAME FROM USER_TAB_COLUMNS
WHERE TABLE_NAME = 'EMPLOYEE' AND COLUMN_NAME = 'MED_ROLE_CODE';


-- =========================================================
-- 남은 것 (이번 범위 아님)
--   - COMMON_CODE 의 ROLE_CD 그룹 12건이 그대로 남아있다. 앱에서는 더 이상 쓰지 않는다.
--     ROLE 테이블과 내용이 중복이므로 정리 여부를 별도로 정할 것.
--   - EMPLOYEE_MED_ROLE_BAK 은 롤백이 필요 없다고 판단되면 삭제해도 된다.
-- =========================================================

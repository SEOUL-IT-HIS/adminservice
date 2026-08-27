-- =========================================================
-- 약제관리(PHM_GROUP) 단일 메뉴로 전환
-- SQLGate로 직접 실행 예정 (이 저장소 코드로는 반영되지 않음 — 기록 목적)
-- 하위 3개(약품 재고관리/조제·불출관리/특수약품 관리)는 안 쓰기로 하고,
-- 약제관리 자체를 /pharmacy/medication 으로 연결되는 단일 메뉴로 바꾼다.
-- (참고: 2026-08-26 기준 hisfrontend에 /pharmacy/medication 페이지는 아직 없음 —
--  /pharmacy/stock, /pharmacy/dispense, /pharmacy/controlled 3개만 존재)
-- =========================================================

-- 약제관리(PHM_GROUP)를 하위메뉴 없는 단일 메뉴로 전환 + 경로 변경
UPDATE MENU SET MENU_URL = '/pharmacy/medication', UPDATED_AT = SYSDATE
WHERE MENU_ID = 'a1e4c2d0-1111-4a1a-9a11-000000000004'; -- 약제관리

-- 하위 3개는 소프트 삭제(USE_YN = 'N') — 물리 삭제 안 함
UPDATE MENU SET USE_YN = 'N', UPDATED_AT = SYSDATE
WHERE MENU_ID = 'd94c123b-7d23-4ed8-91d9-eb8aceecd1dd'; -- 약품 재고관리

UPDATE MENU SET USE_YN = 'N', UPDATED_AT = SYSDATE
WHERE MENU_ID = '09dbc5c4-f6a8-408a-87b2-4645a76a4af0'; -- 약품 조제·불출관리

UPDATE MENU SET USE_YN = 'N', UPDATED_AT = SYSDATE
WHERE MENU_ID = '8b20b55b-d379-4efe-b67a-18cbd15ca1e9'; -- 특수약품 관리

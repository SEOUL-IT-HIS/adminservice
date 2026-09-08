package kr.co.seoulit.his.adminservice.auth.dto;

import lombok.Data;

import java.util.List;

/**
 * [세션에 담는 로그인 사용자]
 *
 * 로그인에 성공하면 이 객체를 HttpSession 에 넣는다.
 * 세션은 Redis 에 저장되고 다른 서비스(billing, patient ...)도 같은 Redis 를 보므로,
 * 결국 이 객체를 그대로 읽게 된다.
 * 즉 여기 적힌 필드 이름이 곧 팀 간 약속이다. 함부로 바꾸면 다른 서비스가 깨진다.
 *
 * ACCOUNT 한 줄을 통째로 담는 AuthDto 를 쓰지 않고 따로 만든 이유가 두 가지다.
 *
 *  1. 비밀번호(pwHash)처럼 세션에 있을 이유가 없는 값까지 Redis 에 들어간다.
 *     Redis 는 같은 사내망에서 들여다볼 수 있으므로 꼭 필요한 것만 담는다.
 *  2. AuthDto 의 Timestamp 필드는 JSON 으로 바뀔 때
 *     ["java.sql.Timestamp", "2026-08-07T07:47:57.535Z"] 처럼 자바 색이 짙은 모양이 된다.
 *     다른 팀이 읽기 어려우므로 여기서는 문자열과 문자열 목록만 쓴다.
 */
@Data
public class SessionUser {

    /**
     * ACCOUNT.ACCOUNT_ID.
     * 다른 서비스가 쓸 일은 없지만, 프론트가 받던 응답 모양을 그대로 두려고 남겨둔다
     * (GET /api/auth/me 가 이 객체를 그대로 내려주기 때문).
     */
    private String accountId;

    /** 계정 상태 (01 = 정상). 위 accountId 와 같은 이유로 남겨둔다. */
    private String accountStatus;

    /** EMPLOYEE.EMP_ID — 다른 서비스가 "작성자" 같은 값으로 저장할 때 쓰는 키 */
    private String empId;

    /** 사번 (예: E202608001) — 화면에 보여줄 때 쓴다 */
    private String empNo;

    /** 직원 이름 */
    private String empName;

    /** 부서 공통코드 (DEPT_CD) */
    private String deptCode;

    /** 로그인 아이디 */
    private String loginId;

    /**
     * 이 사람이 가진 역할 코드들 (예: DOCTOR, NURSE).
     * 화면에서는 한 사람에게 역할 하나만 주지만,
     * DB(EMP_ROLE)는 여러 개를 허용하므로 목록으로 담는다.
     */
    private List<String> roleCodes;

    /**
     * 볼 수 있는 메뉴 코드들 (ROLE_MENU 에서 CAN_READ 가 'Y' 인 것).
     * 다른 서비스는 "이 사람이 이 화면을 써도 되는가"를 이 목록으로 판단한다.
     */
    private List<String> menuCodes;
}

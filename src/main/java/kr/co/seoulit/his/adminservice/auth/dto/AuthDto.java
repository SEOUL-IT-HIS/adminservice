package kr.co.seoulit.his.adminservice.auth.dto;

import lombok.Data;

import java.sql.Timestamp;

/**
 * ACCOUNT / 로그인 관련 DTO
 * - ACCOUNT 테이블 매핑
 * - 로그인 응답 시 empName, deptCode 등 직원 정보도 함께 담을 수 있음
 */
@Data
public class AuthDto {

    // ----- ACCOUNT 테이블 -----
    private String accountId;
    private String empId;
    private String loginId;
    /** 직원등록 시 화면 입력 없음 → 서비스에서 기본 해시 설정 */
    private String pwHash;
    private String accountStatus;
    private Integer failCount;
    private Timestamp lockedAt;
    private Timestamp pwChangeAt;
    private Timestamp lastLoginAt;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // ----- 로그인 응답용 (EMPLOYEE 조인 값) -----
    private String empName;
    private String empNo;
    private String deptCode;

    /**
     * 계정 목록(Permissions > Accounts)용 역할 코드. 쉼표로 이어 붙인다 (예: "03" 또는 "01,02").
     * 화면은 역할 목록 API 로 코드를 이름으로 바꿔 보여준다.
     */
    private String roleCodes;
}

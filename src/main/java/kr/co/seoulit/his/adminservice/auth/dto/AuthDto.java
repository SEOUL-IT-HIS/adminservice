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
}

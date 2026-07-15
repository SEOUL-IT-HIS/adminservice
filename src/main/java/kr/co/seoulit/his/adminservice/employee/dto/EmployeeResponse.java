package kr.co.seoulit.his.adminservice.employee.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 직원 조회/목록 응답 — hisfrontend Employee 타입과 동일 계약
 */
@Getter
@Builder
public class EmployeeResponse {

    private Long empId;
    private String empNo;
    private String name;
    private String email;
    private String phone;
    private String hireDate;
    private String retireDate;
    private String empStatus;
    private String deptCode;
    private String createdAt;
    private String updatedAt;
    private String loginId;
    private String accountStatus;
}

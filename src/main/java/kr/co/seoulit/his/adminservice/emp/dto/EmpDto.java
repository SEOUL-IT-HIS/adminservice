package kr.co.seoulit.his.adminservice.emp.dto;


import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data

public class EmpDto {

    private String empId;
    private String empNo;
    private String empName;
    private String empEmail;
    private String empPhone;
    private Date hireDate;
    private Date retireDate;
    private String empStatus;
    private String deptCode;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String zipCode;
    private String address;
    private String addressDetail;

    /** 배정할 역할 ID 목록 — EMP_ROLE 테이블에 반영된다 */
    private List<String> roleIds;

    /** 역할 부여 주체(관리자) empId — 프론트에서 로그인 사용자 정보로 채워 보냄 */
    private String assignedBy;

    /** 등록 요청에만 담겨오는 평문 주민등록번호 — 절대 Entity에 그대로 저장하지 않는다 (해시로만 변환) */
    private String rrn;

}

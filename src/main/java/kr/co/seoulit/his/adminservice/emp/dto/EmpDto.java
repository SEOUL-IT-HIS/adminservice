package kr.co.seoulit.his.adminservice.emp.dto;


import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

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
    private String medRoleCode;

    /** 등록 요청에만 담겨오는 평문 주민등록번호 — 절대 Entity에 그대로 저장하지 않는다 (해시로만 변환) */
    private String rrn;

}

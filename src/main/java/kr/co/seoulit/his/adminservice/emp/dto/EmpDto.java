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

}

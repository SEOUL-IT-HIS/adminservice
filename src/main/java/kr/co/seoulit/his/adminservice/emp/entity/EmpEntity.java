package kr.co.seoulit.his.adminservice.emp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "EMPLOYEE")
public class EmpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "EMP_ID", length = 36)
    private String empId;

    @Column(name = "EMP_NO")
    private String empNo;

    @Column(name = "NAME")
    private String empName;

    @Column(name = "EMAIL")
    private String empEmail;

    @Column(name = "PHONE")
    private String empPhone;

    @Column(name = "HIRE_DATE")
    private Date hireDate;

    @Column(name = "RETIRE_DATE")
    private Date retireDate;

    @Column(name = "EMP_STATUS")
    private String empStatus;

    @Column(name = "DEPT_CODE")
    private String deptCode;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;
}

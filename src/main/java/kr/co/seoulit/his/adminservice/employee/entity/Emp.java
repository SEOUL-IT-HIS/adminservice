package kr.co.seoulit.his.adminservice.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 기존 ADMIN.EMPLOYEE 테이블 매핑 (SQLGate / FREEPDB1)
 * ACCOUNT.FK_ACCOUNT_EMP_ID → EMPLOYEE.PK_EMPLOYEE
 * PK는 EMP_SEQ.NEXTVAL 을 서비스에서 할당한다.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "EMPLOYEE")
public class Emp {

    @Id
    @Column(name = "EMP_ID")
    private Long empId;

    @Column(name = "EMP_NO", nullable = false, length = 20, unique = true)
    private String empNo;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "EMAIL", length = 200)
    private String email;

    @Column(name = "PHONE", length = 20)
    private String phone;

    @Column(name = "HIRE_DATE")
    private LocalDate hireDate;

    @Column(name = "RETIRE_DATE")
    private LocalDate retireDate;

    /** ACTIVE | LEAVE | RETIRED */
    @Column(name = "EMP_STATUS", length = 20)
    private String empStatus;

    @Column(name = "DEPT_CODE", length = 20)
    private String deptCode;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

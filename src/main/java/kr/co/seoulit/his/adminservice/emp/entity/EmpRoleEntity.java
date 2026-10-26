package kr.co.seoulit.his.adminservice.emp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;


@Entity
@Table(name = "EMP_ROLE")
@Setter
@Getter
@NoArgsConstructor
public class EmpRoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "EMP_ROLE_ID", length = 36)
    private String empRoleId;

    @Column(name = "EMP_ID")
    private String empId;

    @Column(name = "ROLE_ID")
    private String roleId;

    @Column(name = "ASSIGNED_BY")
    private String assignedBy;

    @Column(name = "ASSIGNED_AT")
    private Timestamp assignedAt;

}

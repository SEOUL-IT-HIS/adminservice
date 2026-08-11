package kr.co.seoulit.his.adminservice.role.entity;

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
@Table(name = "ROLE")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ROLE_ID", length = 36)
    private String roleId;

    @Column(name = "ROLE_CODE")
    private String roleCode;

    @Column(name = "ROLE_NAME")
    private String roleName;

    @Column(name = "DESCRIPTION")
    private String roleDescription;

    @Column(name = "USE_YN", length = 1, columnDefinition = "CHAR(1)")
    private String useYn;
}

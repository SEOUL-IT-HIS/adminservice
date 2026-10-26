package kr.co.seoulit.his.adminservice.auth.entity;

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

/**
 * [Entity] 계정 — ACCOUNT 테이블 매핑
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ACCOUNT")
public class AuthEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ACCOUNT_ID", length = 36)
    private String accountId;

    @Column(name = "EMP_ID", nullable = false, length = 36)
    private String empId;

    @Column(name = "LOGIN_ID", nullable = false, length = 50)
    private String loginId;

    @Column(name = "PW_HASH", nullable = false, length = 255)
    private String pwHash;

    @Column(name = "ACCOUNT_STATUS", length = 20)
    private String accountStatus;

    @Column(name = "FAIL_COUNT")
    private Integer failCount;

    @Column(name = "LOCKED_AT")
    private Timestamp lockedAt;

    @Column(name = "PW_CHANGE_AT")
    private Timestamp pwChangeAt;

    @Column(name = "LAST_LOGIN_AT")
    private Timestamp lastLoginAt;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;
}

package kr.co.seoulit.his.adminservice.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 기존 ADMIN.ACCOUNT 테이블 매핑 (SQLGate / FREEPDB1)
 * PK는 ACCOUNT_SEQ.NEXTVAL 을 서비스에서 할당한다.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ACCOUNT")
public class Account {

    @Id
    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Column(name = "EMP_ID", nullable = false, unique = true)
    private Long empId;

    @Column(name = "LOGIN_ID", nullable = false, length = 50, unique = true)
    private String loginId;

    /** BCrypt 해시. 평문 비밀번호는 저장하지 않는다. */
    @Column(name = "PW_HASH", nullable = false, length = 255)
    private String pwHash;

    /** ACTIVE | LOCKED | DISABLED */
    @Column(name = "ACCOUNT_STATUS", length = 20)
    private String accountStatus;

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

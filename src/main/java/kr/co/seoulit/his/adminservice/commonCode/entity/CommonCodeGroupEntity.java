package kr.co.seoulit.his.adminservice.commonCode.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [Entity] 공통코드 그룹 — JPA 테이블 매핑 (COMMON_CODE_GROUP)
 * - Repository.save() 로 DB INSERT/UPDATE
 * - GROUP_ID: Oracle 시퀀스 COMMON_CODE_GROUP_SEQ
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "COMMON_CODE_GROUP")
public class CommonCodeGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "GROUP_ID", length = 36)
    private String groupId;

    @Column(name = "GROUP_CODE")
    private String groupCode;

    @Column(name = "GROUP_NAME")
    private String groupName;

    @Column(name = "USE_YN", length = 1, columnDefinition = "CHAR(1)")
    private String useYn;
}

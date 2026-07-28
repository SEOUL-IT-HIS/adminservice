package kr.co.seoulit.his.adminservice.commonCode.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [Entity] 공통코드 항목 — JPA 테이블 매핑 (COMMON_CODE)
 * - Repository.save() 로 DB INSERT/UPDATE
 * - CODE_ID: Oracle 시퀀스 COMMON_CODE_SEQ
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "COMMON_CODE")
public class CommonCodeItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonCodeItemSeq")
    @SequenceGenerator(
            name = "commonCodeItemSeq",
            sequenceName = "COMMON_CODE_SEQ",
            allocationSize = 1
    )
    @Column(name = "CODE_ID")
    private Long codeId;

    @Column(name = "GROUP_ID")
    private Long groupId;

    @Column(name = "PARENT_CODE_ID")
    private Long parentCodeId;

    @Column(name = "CODE_VALUE")
    private String codeValue;

    @Column(name = "CODE_NAME")
    private String codeName;

    @Column(name = "SORT_ORDER")
    private Integer sortOrder;

    @Column(name = "USE_YN", length = 1, columnDefinition = "CHAR(1)")
    private String useYn;
}

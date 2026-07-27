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
 * 공통코드 항목 (COMMON_CODE)
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "COMMON_CODE")
public class CommonCodeItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

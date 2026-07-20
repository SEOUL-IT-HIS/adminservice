package kr.co.seoulit.his.adminservice.commoncode.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ADMIN.COMMON_CODE
 * - PK: CODE_ID (NUMBER, 시퀀스)
 * - FK: GROUP_ID → COMMON_CODE_GROUP
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "COMMON_CODE")
public class CommonCode {

    @Id
    @Column(name = "CODE_ID")
    private Long codeId;

    @Column(name = "GROUP_ID", nullable = false)
    private Long groupId;

    @Column(name = "PARENT_CODE_ID")
    private Long parentCodeId;

    @Column(name = "CODE_VALUE", nullable = false, length = 50)
    private String codeValue;

    @Column(name = "CODE_NAME", nullable = false, length = 100)
    private String codeName;

    @Column(name = "SORT_ORDER")
    private Integer sortOrder;

    @Column(name = "USE_YN", columnDefinition = "CHAR(1)")
    private String useYn;
}

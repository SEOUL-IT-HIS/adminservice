package kr.co.seoulit.his.adminservice.commoncode.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ADMIN.COMMON_CODE_GROUP
 * - PK: GROUP_ID (NUMBER, 시퀀스)
 * - 업무 키: GROUP_CODE (예: EMP_STATUS_CD)
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "COMMON_CODE_GROUP")
public class CommonCodeGroup {

    @Id
    @Column(name = "GROUP_ID")
    private Long groupId;

    @Column(name = "GROUP_CODE", nullable = false, length = 50, unique = true)
    private String groupCode;

    @Column(name = "GROUP_NAME", nullable = false, length = 100)
    private String groupName;

    @Column(name = "USE_YN", columnDefinition = "CHAR(1)")
    private String useYn;
}

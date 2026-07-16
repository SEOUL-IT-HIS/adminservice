package kr.co.seoulit.his.adminservice.system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 기존 ADMIN.MENU 테이블 매핑 (SQLGate / FREEPDB1)
 * - PARENT_MENU_ID null → L0 업무영역
 * - 트리 조립은 프론트에서 수행한다.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "MENU")
public class Menu {

    @Id
    @Column(name = "MENU_ID")
    private Long menuId;

    @Column(name = "PARENT_MENU_ID")
    private Long parentMenuId;

    @Column(name = "MENU_CODE", nullable = false, length = 50)
    private String menuCode;

    @Column(name = "MENU_NAME", nullable = false, length = 100)
    private String menuName;

    @Column(name = "MENU_URL", length = 200)
    private String menuUrl;

    @Column(name = "SORT_ORDER")
    private Integer sortOrder;

    /** DB: CHAR(1) — VARCHAR 로 두면 ddl-auto=validate 실패 */
    @Column(name = "USE_YN", columnDefinition = "CHAR(1)")
    private String useYn;

    @Column(name = "AREA_KEY", length = 30)
    private String areaKey;

    @Column(name = "SERVICE_CODE", length = 3)
    private String serviceCode;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}

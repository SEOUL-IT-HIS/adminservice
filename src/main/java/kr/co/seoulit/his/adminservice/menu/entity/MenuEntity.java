package kr.co.seoulit.his.adminservice.menu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(name = "MENU")
@Setter
@Getter
@NoArgsConstructor
@Entity
public class MenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MENU_ID", length = 36)
    private String menuId;

    @Column(name = "PARENT_MENU_ID", length = 36)
    private String parentMenuId;

    @Column(name = "MENU_CODE")
    private String menuCode;

    @Column(name = "MENU_NAME")
    private String menuName;

    @Column(name = "MENU_URL")
    private String menuUrl;

    @Column(name = "SORT_ORDER")
    private Integer sortOrder;

    @Column(name = "USE_YN", length = 1, columnDefinition = "CHAR(1)")
    private String useYn;

    @Column(name="AREA_KEY")
    private String areaKey;

    @Column(name = "SERVICE_CODE")
    private String serviceCode;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name="UPDATED_AT")
    private LocalDateTime updatedAt;

}

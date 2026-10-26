package kr.co.seoulit.his.adminservice.roleMenu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [역할별 메뉴 권한]
 * "DOCTOR 역할은 처방 메뉴를 볼 수 있다" 같은 역할↔메뉴 연결을 담는 테이블.
 *
 * 직원(EMPLOYEE)과는 직접 연결되지 않는다.
 * 직원은 EMP_ROLE 로 역할을 받고, 그 역할이 여기서 메뉴 권한을 받는다.
 *
 * ROLE_ID + MENU_ID 조합은 UK_ROLE_MENU 제약으로 유일하다
 * (같은 역할에 같은 메뉴가 두 번 들어갈 수 없다).
 */
@Entity
@Table(name = "ROLE_MENU")
@Getter
@Setter
@NoArgsConstructor
public class RoleMenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ROLE_MENU_ID", length = 36)
    private String roleMenuId;

    /**
     * ROLE.ROLE_ID (FK)
     * @ManyToOne 연관관계를 걸지 않고 값만 들고 있는다.
     * EmpRoleEntity 와 같은 방식 — 연관관계를 걸면 조회할 때 의도치 않은 조인이 딸려온다.
     */
    @Column(name = "ROLE_ID", length = 36)
    private String roleId;

    /** MENU.MENU_ID (FK) */
    @Column(name = "MENU_ID", length = 36)
    private String menuId;

    /**
     * 메뉴 조회(화면 진입) 허용 여부.
     * DB 가 CHAR(1) 이라 boolean 이 아니라 "Y"/"N" 문자열로 다룬다
     * (RoleEntity.useYn, MenuEntity.useYn 과 같은 방식).
     */
    @Column(name = "CAN_READ", length = 1, columnDefinition = "CHAR(1)")
    private String canRead;

    /**
     * 아래 3개는 이번 단계에서 화면이 쓰지 않는다.
     * 항상 "N" 으로 넣어두고, 버튼 단위 제어가 필요해지면 그때 쓴다.
     */
    @Column(name = "CAN_CREATE", length = 1, columnDefinition = "CHAR(1)")
    private String canCreate;

    @Column(name = "CAN_UPDATE", length = 1, columnDefinition = "CHAR(1)")
    private String canUpdate;

    @Column(name = "CAN_DELETE", length = 1, columnDefinition = "CHAR(1)")
    private String canDelete;
}
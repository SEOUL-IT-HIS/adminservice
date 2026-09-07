package kr.co.seoulit.his.adminservice.roleMenu.dto;

import lombok.Data;

/**
 * [권한 페이지 조회 응답] 메뉴 한 줄
 *
 * 엔티티(RoleMenuEntity)를 그대로 내리지 않는 이유:
 * 권한 페이지는 ROLE_MENU 에 행이 없는 메뉴도 (체크 안 된 상태로) 보여줘야 한다.
 * 엔티티만 내리면 아직 권한을 안 준 메뉴는 목록에서 아예 빠져버린다.
 * 그래서 MENU 전체를 기준으로 삼고, 여기에 canRead 만 얹어서 내려준다.
 */
@Data
public class RoleMenuDto {

    private String menuId;

    /** 상위 메뉴 ID. 최상위면 null — 화면에서 트리로 그릴 때 쓴다 */
    private String parentMenuId;

    private String menuName;

    private String menuUrl;

    /** 화면 정렬 순서 (MENU.SORT_ORDER) */
    private Integer sortOrder;

    /**
     * 이 역할이 이 메뉴를 볼 수 있는지. "Y" 또는 "N".
     * ROLE_MENU 에 행이 아예 없으면 "N".
     * (프론트는 useYn 과 마찬가지로 canRead === "Y" 로 비교한다)
     */
    private String canRead;
}
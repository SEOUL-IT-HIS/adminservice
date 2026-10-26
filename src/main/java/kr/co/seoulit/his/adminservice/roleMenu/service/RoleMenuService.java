package kr.co.seoulit.his.adminservice.roleMenu.service;

import kr.co.seoulit.his.adminservice.roleMenu.dto.RoleMenuDto;

import java.util.List;

/**
 * [역할별 메뉴 권한] 서비스
 * 구현은 impl/RoleMenuServiceImpl 에 있다.
 */
public interface RoleMenuService {

    /**
     * 역할 하나의 메뉴 권한 목록.
     * 사용중인 메뉴 전부를 내려주고, 각 메뉴에 이 역할의 canRead 를 얹는다.
     */
    List<RoleMenuDto> selectRoleMenuList(String roleId);

    /**
     * 역할 하나의 메뉴 권한 일괄 저장.
     * menuIds 는 저장 후의 최종 상태이며, 여기 없는 메뉴는 권한이 해제된다.
     */
    void saveRoleMenus(String roleId, List<String> menuIds);
}
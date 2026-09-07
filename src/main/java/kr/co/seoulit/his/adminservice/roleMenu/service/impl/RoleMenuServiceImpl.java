package kr.co.seoulit.his.adminservice.roleMenu.service.impl;

import kr.co.seoulit.his.adminservice.common.exception.BusinessException;
import kr.co.seoulit.his.adminservice.common.exception.ErrorCode;
import kr.co.seoulit.his.adminservice.menu.entity.MenuEntity;
import kr.co.seoulit.his.adminservice.menu.repository.MenuRepository;
import kr.co.seoulit.his.adminservice.role.repository.RoleRepository;
import kr.co.seoulit.his.adminservice.roleMenu.dto.RoleMenuDto;
import kr.co.seoulit.his.adminservice.roleMenu.entity.RoleMenuEntity;
import kr.co.seoulit.his.adminservice.roleMenu.repository.RoleMenuRepository;
import kr.co.seoulit.his.adminservice.roleMenu.service.RoleMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleMenuServiceImpl implements RoleMenuService {

    private final RoleMenuRepository roleMenuRepository;
    private final RoleRepository roleRepository;
    private final MenuRepository menuRepository;

    // ========== [조회] ==========
    @Override
    public List<RoleMenuDto> selectRoleMenuList(String roleId) {
        checkRoleExists(roleId);

        // 이 역할이 읽기 허용된 메뉴 ID 를 먼저 한 번에 모아둔다.
        // 메뉴마다 ROLE_MENU 를 찾아보면 쿼리가 메뉴 수만큼 나간다.
        Set<String> allowedMenuIds = new HashSet<>();
        for (RoleMenuEntity roleMenu : roleMenuRepository.findByRoleId(roleId)) {
            if ("Y".equals(roleMenu.getCanRead())) {
                allowedMenuIds.add(roleMenu.getMenuId());
            }
        }

        // 권한 페이지는 전체 메뉴를 다 그려야 하므로 사용중인 메뉴를 전부 내려준다.
        // 아직 권한이 없는 메뉴는 canRead 가 "N" 으로 나간다.
        List<RoleMenuDto> result = new ArrayList<>();
        for (MenuEntity menu : menuRepository.findByUseYnOrderBySortOrderAsc("Y")) {
            RoleMenuDto dto = new RoleMenuDto();
            dto.setMenuId(menu.getMenuId());
            dto.setParentMenuId(menu.getParentMenuId());
            dto.setMenuName(menu.getMenuName());
            dto.setMenuUrl(menu.getMenuUrl());
            dto.setSortOrder(menu.getSortOrder());
            dto.setCanRead(allowedMenuIds.contains(menu.getMenuId()) ? "Y" : "N");
            result.add(dto);
        }
        return result;
    }

    // ========== [저장] ==========
    @Override
    public void saveRoleMenus(String roleId, List<String> menuIds) {
        checkRoleExists(roleId);

        // menuIds 가 없으면(null) 요청이 잘못된 것으로 본다.
        // 권한을 전부 해제하려면 빈 배열([])을 명시적으로 보내야 한다.
        // null 을 "전부 해제"로 처리하면, 요청 본문이 깨졌을 때 권한이 통째로 날아간다.
        if (menuIds == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        // 1) 먼저 전부 검증한다.
        //    잘못된 메뉴가 하나라도 있으면 여기서 멈추므로 기존 권한은 그대로 남는다.
        for (String menuId : menuIds) {
            menuRepository.findById(menuId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
        }

        // 2) 추가할 것과 뺄 것만 계산한다.
        //    (전부 지우고 다시 넣으면 그대로 유지되는 권한까지 행이 새로 생긴다)
        List<String> existingMenuIds = new ArrayList<>();
        for (RoleMenuEntity roleMenu : roleMenuRepository.findByRoleId(roleId)) {
            existingMenuIds.add(roleMenu.getMenuId());
        }

        // 화면에서 체크된 것 중, DB 에 아직 없는 것 → 새로 넣을 것
        Set<String> toAdd = new HashSet<>();
        for (String menuId : menuIds) {
            if (!existingMenuIds.contains(menuId)) {
                toAdd.add(menuId);
            }
        }

        // DB 에 있는 것 중, 화면에서 체크가 풀린 것 → 지울 것
        Set<String> toRemove = new HashSet<>();
        for (String menuId : existingMenuIds) {
            if (!menuIds.contains(menuId)) {
                toRemove.add(menuId);
            }
        }

        // 3) 뺄 것만 삭제. 빈 목록으로 부르면 IN () 이 되어 SQL 오류가 난다.
        if (!toRemove.isEmpty()) {
            roleMenuRepository.deleteByRoleIdAndMenuIdIn(roleId, toRemove);
        }

        // 4) 추가할 것만 삽입.
        //    이번 단계는 읽기 권한만 쓰므로 CAN_READ 만 "Y" 이고 나머지 셋은 "N" 이다.
        for (String menuId : toAdd) {
            RoleMenuEntity roleMenu = new RoleMenuEntity();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenu.setCanRead("Y");
            roleMenu.setCanCreate("N");
            roleMenu.setCanUpdate("N");
            roleMenu.setCanDelete("N");
            roleMenuRepository.save(roleMenu);
        }
    }

    /** 역할이 실제로 있고 사용중(USE_YN='Y')인지 확인. 아니면 예외 */
    private void checkRoleExists(String roleId) {
        roleRepository.findById(roleId)
                .filter(role -> "Y".equals(role.getUseYn()))
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
    }
}
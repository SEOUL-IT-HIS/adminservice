package kr.co.seoulit.his.adminservice.roleMenu.controller;

import kr.co.seoulit.his.adminservice.common.response.ApiResponse;
import kr.co.seoulit.his.adminservice.roleMenu.dto.RoleMenuDto;
import kr.co.seoulit.his.adminservice.roleMenu.dto.RoleMenuSaveDto;
import kr.co.seoulit.his.adminservice.roleMenu.service.RoleMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [Controller] 역할별 메뉴 권한 API
 * - HTTP 요청 수신 → Service 호출 → ApiResponse 응답
 */
@RestController
@RequiredArgsConstructor
// 옛 경로도 함께 받는다. 이유는 AuthController 의 같은 자리 주석 참고.
@RequestMapping({"/api/admin/role-menu", "/api/role-menu"})
public class RoleMenuController {

    private final RoleMenuService roleMenuService;

    // ========== [조회] GET /api/admin/role-menu/list/{roleId} ==========
    @GetMapping("/list/{roleId}")
    public ApiResponse<List<RoleMenuDto>> getRoleMenuList(@PathVariable String roleId) {
        return ApiResponse.success(roleMenuService.selectRoleMenuList(roleId));
    }

    // ========== [저장] PUT /api/admin/role-menu/save/{roleId} ==========
    @PutMapping("/save/{roleId}")
    public ApiResponse<Void> saveRoleMenus(@PathVariable String roleId,
                                           @RequestBody RoleMenuSaveDto dto) {
        roleMenuService.saveRoleMenus(roleId, dto.getMenuIds());
        return ApiResponse.success(null);
    }
}
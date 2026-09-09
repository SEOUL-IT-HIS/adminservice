package kr.co.seoulit.his.adminservice.role.controller;


import kr.co.seoulit.his.adminservice.common.response.ApiResponse;
import kr.co.seoulit.his.adminservice.role.entity.RoleEntity;
import kr.co.seoulit.his.adminservice.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
// 옛 경로도 함께 받는다. 이유는 AuthController 의 같은 자리 주석 참고.
@RequestMapping({"/api/admin/role", "/api/role"})
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/list")
    public ApiResponse<List<RoleEntity>> getRoleList() {
        return ApiResponse.success(roleService.selectRoleList());
    }
}

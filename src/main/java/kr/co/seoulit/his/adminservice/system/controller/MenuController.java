package kr.co.seoulit.his.adminservice.system.controller;

import kr.co.seoulit.his.adminservice.common.dto.ApiResponse;
import kr.co.seoulit.his.adminservice.system.dto.MenuResponse;
import kr.co.seoulit.his.adminservice.system.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * 메뉴 목록 조회 (flat)
     * GET /api/menus
     * - USE_YN = Y 만 반환
     * - 트리 조립은 프론트에서 수행
     */
    @GetMapping
    public ApiResponse<List<MenuResponse>> list() {
        return ApiResponse.ok(menuService.findAllActive());
    }
}

package kr.co.seoulit.his.adminservice.menu.controller;

import kr.co.seoulit.his.adminservice.common.response.ApiResponse;
import kr.co.seoulit.his.adminservice.menu.entity.MenuEntity;
import kr.co.seoulit.his.adminservice.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;



@RestController
@RequestMapping("/api")
@RequiredArgsConstructor

public class MenuController {

    private final MenuService menuService;

    @GetMapping("/menu")
    public ApiResponse<List<MenuEntity>> getMenuRows() {
        return ApiResponse.success(menuService.selectMenuRows());
    }
}

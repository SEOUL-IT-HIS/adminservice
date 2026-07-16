package kr.co.seoulit.his.adminservice.system.service.impl;

import kr.co.seoulit.his.adminservice.system.dto.MenuResponse;
import kr.co.seoulit.his.adminservice.system.entity.Menu;
import kr.co.seoulit.his.adminservice.system.repository.MenuRepository;
import kr.co.seoulit.his.adminservice.system.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private static final String USE_Y = "Y";

    private final MenuRepository menuRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MenuResponse> findAllActive() {
        return menuRepository.findByUseYnOrderBySortOrderAsc(USE_Y).stream()
                .map(this::toResponse)
                .toList();
    }

    private MenuResponse toResponse(Menu menu) {
        return MenuResponse.builder()
                .menuId(menu.getMenuId())
                .parentMenuId(menu.getParentMenuId())
                .menuCode(menu.getMenuCode())
                .menuName(menu.getMenuName())
                .menuUrl(menu.getMenuUrl())
                .sortOrder(menu.getSortOrder())
                .useYn(menu.getUseYn())
                .areaKey(menu.getAreaKey())
                .serviceCode(menu.getServiceCode())
                .build();
    }
}

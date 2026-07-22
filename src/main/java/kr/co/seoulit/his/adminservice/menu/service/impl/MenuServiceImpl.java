package kr.co.seoulit.his.adminservice.menu.service.impl;


import java.util.List;

import kr.co.seoulit.his.adminservice.menu.entity.MenuEntity;
import kr.co.seoulit.his.adminservice.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import kr.co.seoulit.his.adminservice.menu.service.MenuService;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository repository;

    @Override
    public List<MenuEntity> selectMenuRows() {
        return repository.findByUseYnOrderBySortOrderAsc("Y");
    }
}

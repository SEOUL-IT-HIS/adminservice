package kr.co.seoulit.his.adminservice.menu.service;
import kr.co.seoulit.his.adminservice.menu.entity.MenuEntity;

import java.util.List;


public interface MenuService {
    List<MenuEntity> selectMenuRows() ;
}
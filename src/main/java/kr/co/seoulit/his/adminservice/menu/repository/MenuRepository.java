package kr.co.seoulit.his.adminservice.menu.repository;

import kr.co.seoulit.his.adminservice.menu.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<MenuEntity, String> {
    List<MenuEntity> findByUseYnOrderBySortOrderAsc(String useYn);
}

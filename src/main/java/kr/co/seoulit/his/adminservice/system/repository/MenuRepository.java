package kr.co.seoulit.his.adminservice.system.repository;

import kr.co.seoulit.his.adminservice.system.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByUseYnOrderBySortOrderAsc(String useYn);
}

package kr.co.seoulit.his.adminservice.role.repository;

import kr.co.seoulit.his.adminservice.role.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<RoleEntity, String> {
    List<RoleEntity> findByUseYn(String useYn);
}

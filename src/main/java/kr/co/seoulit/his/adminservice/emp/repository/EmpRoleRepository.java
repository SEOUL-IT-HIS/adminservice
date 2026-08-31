package kr.co.seoulit.his.adminservice.emp.repository;


import kr.co.seoulit.his.adminservice.emp.entity.EmpRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpRoleRepository extends JpaRepository<EmpRoleEntity, String> {
    List<EmpRoleEntity> findByEmpId(String empId);
}

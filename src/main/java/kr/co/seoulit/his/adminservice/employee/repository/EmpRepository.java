package kr.co.seoulit.his.adminservice.employee.repository;

import kr.co.seoulit.his.adminservice.employee.entity.Emp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpRepository extends JpaRepository<Emp, Long> {

    boolean existsByEmpNo(String empNo);
}

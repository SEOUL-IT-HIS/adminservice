package kr.co.seoulit.his.adminservice.emp.repository;


import kr.co.seoulit.his.adminservice.emp.entity.EmpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpRepository extends JpaRepository<EmpEntity, Long> {

}

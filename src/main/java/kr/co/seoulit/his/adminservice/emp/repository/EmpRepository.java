package kr.co.seoulit.his.adminservice.emp.repository;


import kr.co.seoulit.his.adminservice.emp.entity.EmpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpRepository extends JpaRepository<EmpEntity, String> {

    // 해당 연도 접두사(EMP_NO)로 가장 마지막(최대) 사번 조회 — 자동채번용
    Optional<EmpEntity> findTopByEmpNoStartingWithOrderByEmpNoDesc(String empNoPrefix);

    // 주민등록번호 해시 중복 체크 — 평문은 저장하지 않고 해시값끼리만 비교
    boolean existsByRrnHash(String rrnHash);
}

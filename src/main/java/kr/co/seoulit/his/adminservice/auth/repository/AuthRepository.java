package kr.co.seoulit.his.adminservice.auth.repository;

import kr.co.seoulit.his.adminservice.auth.entity.AuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<AuthEntity, String> {

    Optional<AuthEntity> findByLoginId(String loginId);

    /** 직원 한 명당 계정은 하나다 (ACCOUNT.EMP_ID 에 UNIQUE 제약) */
    Optional<AuthEntity> findByEmpId(String empId);
}

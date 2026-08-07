package kr.co.seoulit.his.adminservice.auth.repository;

import kr.co.seoulit.his.adminservice.auth.entity.AuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<AuthEntity, String> {

    Optional<AuthEntity> findByLoginId(String loginId);
}

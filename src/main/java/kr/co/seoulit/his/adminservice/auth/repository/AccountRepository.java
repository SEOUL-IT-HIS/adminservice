package kr.co.seoulit.his.adminservice.auth.repository;

import kr.co.seoulit.his.adminservice.auth.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByLoginId(String loginId);

    Optional<Account> findByLoginId(String loginId);

    Optional<Account> findByEmpId(Long empId);

    List<Account> findByEmpIdIn(List<Long> empIds);
}

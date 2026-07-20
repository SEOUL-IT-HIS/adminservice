package kr.co.seoulit.his.adminservice.commoncode.repository;

import kr.co.seoulit.his.adminservice.commoncode.entity.CommonCodeGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommonCodeGroupRepository extends JpaRepository<CommonCodeGroup, Long> {

    Optional<CommonCodeGroup> findByGroupCode(String groupCode);

    boolean existsByGroupCode(String groupCode);

    List<CommonCodeGroup> findAllByOrderByGroupCodeAsc();
}

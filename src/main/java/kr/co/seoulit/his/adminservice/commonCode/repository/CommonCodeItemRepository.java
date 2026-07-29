package kr.co.seoulit.his.adminservice.commonCode.repository;

import kr.co.seoulit.his.adminservice.commonCode.entity.CommonCodeItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommonCodeItemRepository extends JpaRepository<CommonCodeItemEntity, Long> {

    /** 그룹별 항목 목록 — useYn Y/N 모두 (관리 화면용) */
    List<CommonCodeItemEntity> findByGroupIdOrderByCodeIdAsc(Long groupId);
}

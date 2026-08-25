package kr.co.seoulit.his.adminservice.commonCode.repository;

import kr.co.seoulit.his.adminservice.commonCode.entity.CommonCodeItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommonCodeItemRepository extends JpaRepository<CommonCodeItemEntity, String> {

    /**
     * 그룹별 항목 목록 — useYn Y/N 모두 (관리 화면용)
     * CODE_ID(PK)는 UUID라 정렬 기준으로 쓸 수 없다 — SORT_ORDER 기준으로 정렬한다.
     */
    List<CommonCodeItemEntity> findByGroupIdOrderBySortOrderAsc(String groupId);
}

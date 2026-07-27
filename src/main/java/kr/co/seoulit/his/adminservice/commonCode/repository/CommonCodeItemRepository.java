package kr.co.seoulit.his.adminservice.commonCode.repository;

import kr.co.seoulit.his.adminservice.commonCode.entity.CommonCodeItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommonCodeItemRepository extends JpaRepository<CommonCodeItemEntity, Long> {

    /** 그룹별 사용중 항목 목록 (정렬순서 오름차순) */
    List<CommonCodeItemEntity> findByGroupIdAndUseYnOrderByCodeIdAsc(Long codeId, String useYn);
}

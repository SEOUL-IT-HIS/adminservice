package kr.co.seoulit.his.adminservice.commonCode.repository;

import kr.co.seoulit.his.adminservice.commonCode.entity.CommonCodeGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * [Repository] 공통코드 그룹 — JPA DB 접근
 * - save() : 등록 (JpaRepository 기본 제공)
 * - findByUseYnOrderByGroupIdAsc() : 목록 조회
 */
public interface CommonCodeGroupRepository extends JpaRepository<CommonCodeGroupEntity, Long> {

    /** [목록] 사용중인 그룹 목록 (그룹ID 오름차순) */
    List<CommonCodeGroupEntity> findByUseYnOrderByGroupIdAsc(String useYn);
}

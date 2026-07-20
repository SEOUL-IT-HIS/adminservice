package kr.co.seoulit.his.adminservice.commoncode.repository;

import kr.co.seoulit.his.adminservice.commoncode.entity.CommonCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommonCodeRepository extends JpaRepository<CommonCode, Long> {

    List<CommonCode> findByGroupIdOrderBySortOrderAscCodeValueAsc(Long groupId);

    List<CommonCode> findByGroupIdAndUseYnOrderBySortOrderAscCodeValueAsc(Long groupId, String useYn);

    boolean existsByGroupId(Long groupId);

    boolean existsByGroupIdAndCodeValue(Long groupId, String codeValue);

    boolean existsByGroupIdAndCodeValueAndCodeIdNot(Long groupId, String codeValue, Long codeId);
}

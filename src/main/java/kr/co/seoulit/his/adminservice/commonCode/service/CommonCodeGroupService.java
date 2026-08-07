package kr.co.seoulit.his.adminservice.commonCode.service;

import kr.co.seoulit.his.adminservice.commonCode.dto.CommonCodeGroupDto;
import kr.co.seoulit.his.adminservice.commonCode.entity.CommonCodeGroupEntity;

import java.util.List;

/**
 * [Service] 공통코드 그룹 — 인터페이스 (메서드 선언만)
 * - 구현체는 CommonCodeGroupServiceImpl
 */
public interface CommonCodeGroupService {

    // [목록]
    List<CommonCodeGroupEntity> selectCommonCodeGroupList();

    // [등록]
    CommonCodeGroupEntity insertCommonCodeGroup(CommonCodeGroupDto dto);

    // [수정]
    CommonCodeGroupEntity updateCommonCodeGroup(String groupId, CommonCodeGroupDto dto);
}



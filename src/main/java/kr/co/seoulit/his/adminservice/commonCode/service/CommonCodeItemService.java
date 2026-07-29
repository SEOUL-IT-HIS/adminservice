package kr.co.seoulit.his.adminservice.commonCode.service;

import kr.co.seoulit.his.adminservice.commonCode.dto.CommonCodeItemDto;
import kr.co.seoulit.his.adminservice.commonCode.entity.CommonCodeItemEntity;

import java.util.List;

public interface CommonCodeItemService {
    //[목록]
    List<CommonCodeItemEntity> selectCommonCodeItemList(Long groupId);

    // [등록]
    CommonCodeItemEntity insertCommonCodeItem(CommonCodeItemDto dto);

    // [수정]
    CommonCodeItemEntity updateCommonCodeItem(Long codeId, CommonCodeItemDto dto);
}

package kr.co.seoulit.his.adminservice.commonCode.service;

import kr.co.seoulit.his.adminservice.commonCode.entity.CommonCodeItemEntity;

import java.util.List;

public interface CommonCodeItemService {

    List<CommonCodeItemEntity> selectCommonCodeItemList(Long groupId);
}

package kr.co.seoulit.his.adminservice.commoncode.service;

import kr.co.seoulit.his.adminservice.commoncode.dto.CommonCodeGroupResponse;
import kr.co.seoulit.his.adminservice.commoncode.dto.CreateCommonCodeGroupRequest;
import kr.co.seoulit.his.adminservice.commoncode.dto.UpdateCommonCodeGroupRequest;

import java.util.List;

public interface CommonCodeGroupService {

    List<CommonCodeGroupResponse> findAll();

    CommonCodeGroupResponse findById(Long groupId);

    CommonCodeGroupResponse create(CreateCommonCodeGroupRequest request);

    CommonCodeGroupResponse update(Long groupId, UpdateCommonCodeGroupRequest request);

    void delete(Long groupId);
}

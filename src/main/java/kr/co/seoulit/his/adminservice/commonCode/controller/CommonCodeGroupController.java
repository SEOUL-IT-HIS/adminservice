package kr.co.seoulit.his.adminservice.commonCode.controller;

import kr.co.seoulit.his.adminservice.common.response.ApiResponse;
import kr.co.seoulit.his.adminservice.commonCode.dto.CommonCodeGroupDto;
import kr.co.seoulit.his.adminservice.commonCode.entity.CommonCodeGroupEntity;
import kr.co.seoulit.his.adminservice.commonCode.service.CommonCodeGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [Controller] 공통코드 그룹 API
 * - HTTP 요청 수신 → Service 호출 → ApiResponse 응답
 */
@RestController
@RequestMapping("/api/commonCodeGroup")
@RequiredArgsConstructor
public class CommonCodeGroupController {

    private final CommonCodeGroupService commonCodeGroupService;

    // ========== [목록] GET /api/commonCodeGroup/list ==========
    @GetMapping("/list")
    public ApiResponse<List<CommonCodeGroupEntity>> getCommonCodeGroupList() {
        return ApiResponse.success(commonCodeGroupService.selectCommonCodeGroupList());
    }

    // ========== [등록] POST /api/commonCodeGroup/register ==========
    @PostMapping("/register")
    public ApiResponse<CommonCodeGroupEntity> createCommonCodeGroup(@RequestBody CommonCodeGroupDto dto) {
        return ApiResponse.success(commonCodeGroupService.insertCommonCodeGroup(dto));
    }
    // ========== [수정] PUT /api/commonCodeGroup/update/{groupId} ==========
    @PutMapping("/update/{groupId}")
    public ApiResponse<CommonCodeGroupEntity> updateCommonCodeGroup(@PathVariable String groupId, @RequestBody CommonCodeGroupDto dto) {
        return ApiResponse.success(commonCodeGroupService.updateCommonCodeGroup(groupId, dto));
    }
}

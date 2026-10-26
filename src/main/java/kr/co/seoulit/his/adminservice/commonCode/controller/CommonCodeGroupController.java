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
/*
 * 경로를 두 개 받는다.
 *
 * 원래 /api/commonCodeGroup 이었는데, 10개 MSA 경로 규칙에 맞추려고
 * /api/admin/commonCodeGroup 으로 옮기는 중이다.
 *
 * 옛 경로를 같이 남겨두는 이유: 이 API 는 우리 프론트만 부르는 게 아니라
 * 다른 MSA 들이 기동할 때도 부른다. 옛 경로를 지우면 그 서비스들이 404 를 받아
 * 아예 뜨지 못한다. 각 팀이 새 경로로 옮긴 것을 확인한 뒤에 옛 경로를 지운다.
 */
@RequestMapping({"/api/admin/commonCodeGroup", "/api/commonCodeGroup"})
@RequiredArgsConstructor
public class CommonCodeGroupController {

    private final CommonCodeGroupService commonCodeGroupService;

    // ========== [목록] GET /api/admin/commonCodeGroup/list ==========
    @GetMapping("/list")
    public ApiResponse<List<CommonCodeGroupEntity>> getCommonCodeGroupList() {
        return ApiResponse.success(commonCodeGroupService.selectCommonCodeGroupList());
    }

    // ========== [등록] POST /api/admin/commonCodeGroup/register ==========
    @PostMapping("/register")
    public ApiResponse<CommonCodeGroupEntity> createCommonCodeGroup(@RequestBody CommonCodeGroupDto dto) {
        return ApiResponse.success(commonCodeGroupService.insertCommonCodeGroup(dto));
    }
    // ========== [수정] PUT /api/admin/commonCodeGroup/update/{groupId} ==========
    @PutMapping("/update/{groupId}")
    public ApiResponse<CommonCodeGroupEntity> updateCommonCodeGroup(@PathVariable String groupId, @RequestBody CommonCodeGroupDto dto) {
        return ApiResponse.success(commonCodeGroupService.updateCommonCodeGroup(groupId, dto));
    }
}

package kr.co.seoulit.his.adminservice.commoncode.controller;

import kr.co.seoulit.his.adminservice.common.dto.ApiResponse;
import kr.co.seoulit.his.adminservice.commoncode.dto.CommonCodeGroupResponse;
import kr.co.seoulit.his.adminservice.commoncode.dto.CreateCommonCodeGroupRequest;
import kr.co.seoulit.his.adminservice.commoncode.dto.UpdateCommonCodeGroupRequest;
import kr.co.seoulit.his.adminservice.commoncode.service.CommonCodeGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/common-code-groups")
@RequiredArgsConstructor
public class CommonCodeGroupController {

    private final CommonCodeGroupService commonCodeGroupService;

    /** GET /api/common-code-groups */
    @GetMapping
    public ApiResponse<List<CommonCodeGroupResponse>> list() {
        return ApiResponse.ok(commonCodeGroupService.findAll());
    }

    /** GET /api/common-code-groups/{groupId} */
    @GetMapping("/{groupId}")
    public ApiResponse<CommonCodeGroupResponse> detail(@PathVariable Long groupId) {
        return ApiResponse.ok(commonCodeGroupService.findById(groupId));
    }

    /** POST /api/common-code-groups */
    @PostMapping
    public ApiResponse<CommonCodeGroupResponse> create(@RequestBody CreateCommonCodeGroupRequest request) {
        return ApiResponse.ok(commonCodeGroupService.create(request));
    }

    /** PUT /api/common-code-groups/{groupId} */
    @PutMapping("/{groupId}")
    public ApiResponse<CommonCodeGroupResponse> update(
            @PathVariable Long groupId,
            @RequestBody UpdateCommonCodeGroupRequest request
    ) {
        return ApiResponse.ok(commonCodeGroupService.update(groupId, request));
    }

    /** DELETE /api/common-code-groups/{groupId} */
    @DeleteMapping("/{groupId}")
    public ApiResponse<Void> delete(@PathVariable Long groupId) {
        commonCodeGroupService.delete(groupId);
        return ApiResponse.ok(null);
    }
}

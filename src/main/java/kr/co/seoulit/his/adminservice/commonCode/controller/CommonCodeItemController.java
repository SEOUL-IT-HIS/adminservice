package kr.co.seoulit.his.adminservice.commonCode.controller;

import kr.co.seoulit.his.adminservice.common.response.ApiResponse;

import kr.co.seoulit.his.adminservice.commonCode.dto.CommonCodeItemDto;
import kr.co.seoulit.his.adminservice.commonCode.entity.CommonCodeItemEntity;
import kr.co.seoulit.his.adminservice.commonCode.service.CommonCodeItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commonCodeItem")
@RequiredArgsConstructor
public class CommonCodeItemController {

    private final CommonCodeItemService commonCodeItemService;

    @GetMapping("/list")
    public ApiResponse<List<CommonCodeItemEntity>> getCommonCodeItemList(
            @RequestParam String groupId
    ) {
        return ApiResponse.success(commonCodeItemService.selectCommonCodeItemList(groupId));
    }
    // ========== [등록] POST /api/commonCodeItem/register ==========
    @PostMapping("/register")
    public ApiResponse<CommonCodeItemEntity> createCommonCodeItem(@RequestBody CommonCodeItemDto dto) {
        return ApiResponse.success(commonCodeItemService.insertCommonCodeItem(dto));
    }
    // ========== [수정] PUT /api/commonCodeItem/update/{codeId} ==========
    @PutMapping("/update/{codeId}")
    public ApiResponse<CommonCodeItemEntity> updateCommonCodeItem(@PathVariable String codeId, @RequestBody CommonCodeItemDto dto) {
        return ApiResponse.success(commonCodeItemService.updateCommonCodeItem(codeId, dto));
    }
}

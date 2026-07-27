package kr.co.seoulit.his.adminservice.commonCode.controller;

import kr.co.seoulit.his.adminservice.common.response.ApiResponse;
import kr.co.seoulit.his.adminservice.commonCode.entity.CommonCodeGroupEntity;
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
            @RequestParam Long groupId
    ) {
        return ApiResponse.success(commonCodeItemService.selectCommonCodeItemList(groupId));
    }

}

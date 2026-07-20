package kr.co.seoulit.his.adminservice.commoncode.controller;

import kr.co.seoulit.his.adminservice.common.dto.ApiResponse;
import kr.co.seoulit.his.adminservice.commoncode.dto.CommonCodeResponse;
import kr.co.seoulit.his.adminservice.commoncode.dto.CreateCommonCodeRequest;
import kr.co.seoulit.his.adminservice.commoncode.dto.UpdateCommonCodeRequest;
import kr.co.seoulit.his.adminservice.commoncode.service.CommonCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/common-codes")
@RequiredArgsConstructor
public class CommonCodeController {

    private final CommonCodeService commonCodeService;

    /**
     * 공통코드 항목 목록
     * GET /api/common-codes?groupCode=DEPT_CD&useYn=Y&keyword=
     */
    @GetMapping
    public ApiResponse<List<CommonCodeResponse>> list(
            @RequestParam String groupCode,
            @RequestParam(required = false) String useYn,
            @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.ok(commonCodeService.findByGroupCode(groupCode, useYn, keyword));
    }

    /** GET /api/common-codes/{codeId} */
    @GetMapping("/{codeId}")
    public ApiResponse<CommonCodeResponse> detail(@PathVariable Long codeId) {
        return ApiResponse.ok(commonCodeService.findById(codeId));
    }

    /** POST /api/common-codes */
    @PostMapping
    public ApiResponse<CommonCodeResponse> create(@RequestBody CreateCommonCodeRequest request) {
        return ApiResponse.ok(commonCodeService.create(request));
    }

    /** PUT /api/common-codes/{codeId} */
    @PutMapping("/{codeId}")
    public ApiResponse<CommonCodeResponse> update(
            @PathVariable Long codeId,
            @RequestBody UpdateCommonCodeRequest request
    ) {
        return ApiResponse.ok(commonCodeService.update(codeId, request));
    }

    /** DELETE /api/common-codes/{codeId} */
    @DeleteMapping("/{codeId}")
    public ApiResponse<Void> delete(@PathVariable Long codeId) {
        commonCodeService.delete(codeId);
        return ApiResponse.ok(null);
    }
}

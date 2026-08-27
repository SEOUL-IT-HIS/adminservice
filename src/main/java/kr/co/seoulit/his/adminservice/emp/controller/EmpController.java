package kr.co.seoulit.his.adminservice.emp.controller;

import kr.co.seoulit.his.adminservice.common.response.ApiResponse;
import kr.co.seoulit.his.adminservice.emp.dto.EmpDto;
import kr.co.seoulit.his.adminservice.emp.dto.RrnCheckResultDto;
import kr.co.seoulit.his.adminservice.emp.entity.EmpEntity;
import kr.co.seoulit.his.adminservice.emp.service.EmpService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * [Controller] 직원 정보 API
 * - HTTP 요청 수신 → Service 호출 → ApiResponse 응답
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emp")
public class EmpController {

    private final EmpService empService;

    // ========== [목록] GET /api/emp/list ==========
    @GetMapping("/list")
    public ApiResponse<List<EmpEntity>> getEmpList() {
        return ApiResponse.success(empService.selectEmpList());
    }

    // ========== [등록] POST /api/emp/register (multipart) ==========
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<EmpEntity> createEmp(
            @RequestPart("dto") EmpDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ApiResponse.success(empService.createEmp(dto, image));
    }


    // ========== [주민등록번호 확인] POST /api/emp/check-rrn ==========
    // dto의 rrn 필드만 사용. 다른 값(이름/부서 등)은 무시하고, 저장도 하지 않는다.
    // 응답엔 중복 여부 + 생년월일만 담기고, 원본 주민번호는 절대 안 돌아간다.
    @PostMapping("/check-rrn")
    public ApiResponse<RrnCheckResultDto> checkRrnDuplicate(@RequestBody EmpDto dto) {
        return ApiResponse.success(empService.checkRrn(dto.getRrn()));
    }

    // ========== [상세] GET /api/emp/detail/{empId}  ==========
     @GetMapping("/detail/{empId}")
     public ApiResponse<EmpEntity> getEmpDetail(@PathVariable String empId) {
         return ApiResponse.success(empService.getEmpById(empId));
     }

    // ========== [수정] PUT /api/emp/update/{empId} ==========
// ========== [수정] PUT /api/emp/update/{empId} (multipart) ==========
    @PutMapping(value = "/update/{empId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<EmpEntity> updateEmp(
            @PathVariable String empId,
            @RequestPart("dto") EmpDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ApiResponse.success(empService.updateEmp(empId, dto, image));
    }
}

package kr.co.seoulit.his.adminservice.emp.controller;

import kr.co.seoulit.his.adminservice.common.response.ApiResponse;
import kr.co.seoulit.his.adminservice.emp.dto.EmpDto;
import kr.co.seoulit.his.adminservice.emp.entity.EmpEntity;
import kr.co.seoulit.his.adminservice.emp.service.EmpService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    // ========== [등록] POST /api/emp/register ==========
    @PostMapping("/register")
    public ApiResponse<EmpEntity> createEmp(@RequestBody EmpDto dto) {
        return ApiResponse.success(empService.createEmp(dto));
    }

    // ========== [수정] PUT /api/emp/update/{empId} ==========
    @PutMapping("/update/{empId}")
    public ApiResponse<EmpEntity> updateEmp(@PathVariable Long empId, @RequestBody EmpDto dto) {
        return ApiResponse.success(empService.updateEmp(empId, dto));
    }

    // ========== [상세] GET /api/emp/detail/{empId} — 미구현 ==========
    // @GetMapping("/detail/{empId}")
    // public ApiResponse<EmpEntity> getEmpDetail(@PathVariable Long empId) {
    //     return ApiResponse.success(empService.getEmpByEmpId(empId));
    // }
}

package kr.co.seoulit.his.adminservice.employee.controller;

import jakarta.validation.Valid;
import kr.co.seoulit.his.adminservice.common.dto.ApiResponse;
import kr.co.seoulit.his.adminservice.employee.dto.CreateEmployeeRequest;
import kr.co.seoulit.his.adminservice.employee.dto.EmployeeResponse;
import kr.co.seoulit.his.adminservice.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * 직원 등록
     * POST /api/employees
     */
    @PostMapping
    public ApiResponse<EmployeeResponse> create(@Valid @RequestBody CreateEmployeeRequest request) {
        return ApiResponse.ok(employeeService.create(request));
    }

    /**
     * 직원 목록 조회 (등록 연동 확인용 초안)
     * GET /api/employees
     */
    @GetMapping
    public ApiResponse<List<EmployeeResponse>> list() {
        return ApiResponse.ok(employeeService.findAll());
    }
}

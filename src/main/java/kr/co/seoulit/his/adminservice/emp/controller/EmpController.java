package kr.co.seoulit.his.adminservice.emp.controller;

import kr.co.seoulit.his.adminservice.auth.dto.AuthDto;
import kr.co.seoulit.his.adminservice.auth.service.AuthService;
import kr.co.seoulit.his.adminservice.common.exception.BusinessException;
import kr.co.seoulit.his.adminservice.common.exception.ErrorCode;
import kr.co.seoulit.his.adminservice.common.response.ApiResponse;
import kr.co.seoulit.his.adminservice.emp.dto.EmpDto;
import kr.co.seoulit.his.adminservice.emp.dto.RrnCheckResultDto;
import kr.co.seoulit.his.adminservice.emp.entity.EmpEntity;
import kr.co.seoulit.his.adminservice.emp.service.EmpService;
import kr.co.seoulit.his.common.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.List;

/**
 * [Controller] 직원 정보 API
 * - HTTP 요청 수신 → Service 호출 → ApiResponse 응답
 */
@RestController
@RequiredArgsConstructor
// 옛 경로도 함께 받는다. 이유는 AuthController 의 같은 자리 주석 참고.
@RequestMapping({"/api/admin/emp", "/api/emp"})
public class EmpController {

    /** ROLE.ROLE_CODE — 시스템 관리자. 계정 잠금 해제는 이 역할만 할 수 있다 */
    private static final String ROLE_CODE_ADMIN = "01";

    private final EmpService empService;
    private final AuthService authService;

    // ========== [목록] GET /api/admin/emp/list ==========
    @GetMapping("/list")
    public ApiResponse<List<EmpEntity>> getEmpList() {
        return ApiResponse.success(empService.selectEmpList());
    }

    // ========== [등록] POST /api/admin/emp/register (multipart) ==========
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<EmpEntity> createEmp(
            @RequestPart("dto") EmpDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ApiResponse.success(empService.createEmp(dto, image));
    }


    // ========== [주민등록번호 확인] POST /api/admin/emp/check-rrn ==========
    // dto의 rrn 필드만 사용. 다른 값(이름/부서 등)은 무시하고, 저장도 하지 않는다.
    // 응답엔 중복 여부 + 생년월일만 담기고, 원본 주민번호는 절대 안 돌아간다.
    @PostMapping("/check-rrn")
    public ApiResponse<RrnCheckResultDto> checkRrnDuplicate(@RequestBody EmpDto dto) {
        return ApiResponse.success(empService.checkRrn(dto.getRrn()));
    }

    // ========== [상세] GET /api/admin/emp/detail/{empId}  ==========
     @GetMapping("/detail/{empId}")
     public ApiResponse<EmpEntity> getEmpDetail(@PathVariable String empId) {
         return ApiResponse.success(empService.getEmpById(empId));
     }

    // ========== [수정] PUT /api/admin/emp/update/{empId} ==========
// ========== [수정] PUT /api/admin/emp/update/{empId} (multipart) ==========
    @PutMapping(value = "/update/{empId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<EmpEntity> updateEmp(
            @PathVariable String empId,
            @RequestPart("dto") EmpDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ApiResponse.success(empService.updateEmp(empId, dto, image));
    }

    // ========== [계정 조회] GET /api/admin/emp/account/{empId} ==========
    // 잠금 시각(lockedAt)·실패 횟수(failCount)를 화면에 보여주려고 쓴다. 비밀번호는 비워서 내려간다.
    @GetMapping("/account/{empId}")
    public ApiResponse<AuthDto> getEmpAccount(@PathVariable String empId) {
        return ApiResponse.success(authService.getAccount(empId));
    }

    // ========== [잠금 해제] PUT /api/admin/emp/unlock/{empId} ==========
    // 시스템 관리자(01)만 가능. 세션에 담긴 역할 코드("01" 또는 "01,02" 형태)로 확인한다.
    @PutMapping("/unlock/{empId}")
    public ApiResponse<Void> unlockAccount(@PathVariable String empId, HttpSession session) {
        SessionUser loginUser = (SessionUser) session.getAttribute(AuthService.SESSION_USER_KEY);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.AUTH_LOGIN_REQUIRED);
        }

        List<String> roleCodes = Arrays.asList(String.valueOf(loginUser.getRoleCodes()).split(","));
        if (!roleCodes.contains(ROLE_CODE_ADMIN)) {
            throw new BusinessException(ErrorCode.AUTH_ADMIN_ONLY);
        }

        authService.unlockAccount(empId);
        return ApiResponse.success(null);
    }
}

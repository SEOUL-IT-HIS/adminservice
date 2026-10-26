package kr.co.seoulit.his.adminservice.auth.controller;

import kr.co.seoulit.his.adminservice.auth.dto.AuthDto;
import kr.co.seoulit.his.adminservice.auth.service.AuthService;
import kr.co.seoulit.his.adminservice.common.exception.BusinessException;
import kr.co.seoulit.his.adminservice.common.exception.ErrorCode;
import kr.co.seoulit.his.adminservice.common.response.ApiResponse;
import kr.co.seoulit.his.common.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * [Controller] 계정 관리 API — 화면: Permissions > Accounts 탭 (IH2-113)
 *
 * - 목록 GET /api/admin/account/list          : 시스템 관리자(01)·개인정보보호 책임자(02)
 * - 해제 PUT /api/admin/account/unlock/{empId} : 시스템 관리자(01)만
 * - 비밀번호 초기화 PUT /api/admin/account/reset-password/{empId} : 시스템 관리자(01)만 (IH2-116)
 *
 * 로그인 여부는 AuthSessionInterceptor 가 먼저 막는다. 여기서는 "어떤 역할인가"만 본다.
 * 역할은 세션(SessionUser.roleCodes)에 "01" 또는 "01,02" 같은 쉼표 문자열로 들어 있다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/account")
public class AccountController {

    /** ROLE.ROLE_CODE — 시스템 관리자 */
    private static final String ROLE_CODE_ADMIN = "01";

    /** ROLE.ROLE_CODE — 개인정보보호 책임자. 감사 업무상 잠금 현황은 볼 수 있다 */
    private static final String ROLE_CODE_PRIVACY_OFFICER = "02";

    private final AuthService authService;

    // ========== [목록] GET /api/admin/account/list ==========
    @GetMapping("/list")
    public ApiResponse<List<AuthDto>> getAccountList(HttpSession session) {
        List<String> roleCodes = findLoginRoleCodes(session);
        if (!roleCodes.contains(ROLE_CODE_ADMIN) && !roleCodes.contains(ROLE_CODE_PRIVACY_OFFICER)) {
            throw new BusinessException(ErrorCode.AUTH_ADMIN_ONLY);
        }
        return ApiResponse.success(authService.getAccountList());
    }

    // ========== [잠금 해제] PUT /api/admin/account/unlock/{empId} ==========
    @PutMapping("/unlock/{empId}")
    public ApiResponse<Void> unlockAccount(@PathVariable String empId, HttpSession session) {
        List<String> roleCodes = findLoginRoleCodes(session);
        if (!roleCodes.contains(ROLE_CODE_ADMIN)) {
            throw new BusinessException(ErrorCode.AUTH_ADMIN_ONLY);
        }
        authService.unlockAccount(empId);
        return ApiResponse.success(null);
    }

    // ========== [비밀번호 초기화] PUT /api/admin/account/reset-password/{empId} (IH2-116) ==========
    // 초기값(1111)으로 되돌리고 잠금도 푼다. 대상이 관리자(01) 계정이면 서비스에서 403.
    @PutMapping("/reset-password/{empId}")
    public ApiResponse<Void> resetPassword(@PathVariable String empId, HttpSession session) {
        List<String> roleCodes = findLoginRoleCodes(session);
        if (!roleCodes.contains(ROLE_CODE_ADMIN)) {
            throw new BusinessException(ErrorCode.AUTH_ADMIN_ONLY);
        }
        authService.resetPassword(empId);
        return ApiResponse.success(null);
    }

    /** 로그인한 사람의 역할 코드 목록. 세션이 없으면 401 */
    private List<String> findLoginRoleCodes(HttpSession session) {
        SessionUser loginUser = (SessionUser) session.getAttribute(AuthService.SESSION_USER_KEY);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.AUTH_LOGIN_REQUIRED);
        }
        return Arrays.asList(String.valueOf(loginUser.getRoleCodes()).split(","));
    }
}

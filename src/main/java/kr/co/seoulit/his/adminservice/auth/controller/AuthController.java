package kr.co.seoulit.his.adminservice.auth.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.co.seoulit.his.adminservice.auth.dto.LoginRequest;
import kr.co.seoulit.his.adminservice.auth.dto.LoginResponse;
import kr.co.seoulit.his.adminservice.auth.service.AuthService;
import kr.co.seoulit.his.adminservice.auth.session.AuthSessionKeys;
import kr.co.seoulit.his.adminservice.common.dto.ApiResponse;
import kr.co.seoulit.his.adminservice.common.exception.BusinessException;
import kr.co.seoulit.his.adminservice.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인
     * POST /api/auth/login
     * - ID/PW 검증 후 HttpSession 에 LOGIN_USER 저장
     * - 응답 Set-Cookie 로 JSESSIONID 전달 (브라우저가 이후 요청에 자동 포함)
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session
    ) {
        LoginResponse user = authService.login(request);
        session.setAttribute(AuthSessionKeys.LOGIN_USER, user);
        return ApiResponse.ok(user);
    }

    /**
     * 로그아웃
     * POST /api/auth/logout
     * - 서버 HttpSession 무효화
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session) {
        session.invalidate();
        return ApiResponse.of(200, "SUCCESS", null);
    }

    /**
     * 현재 로그인 사용자 조회
     * GET /api/auth/me
     * - HttpSession 의 LOGIN_USER 반환
     * - 세션 없으면 Interceptor 또는 아래에서 ADM008
     */
    @GetMapping("/me")
    public ApiResponse<LoginResponse> me(HttpSession session) {
        Object loginUser = session.getAttribute(AuthSessionKeys.LOGIN_USER);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return ApiResponse.ok((LoginResponse) loginUser);
    }
}

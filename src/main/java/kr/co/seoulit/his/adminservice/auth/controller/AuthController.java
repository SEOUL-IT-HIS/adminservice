package kr.co.seoulit.his.adminservice.auth.controller;

import kr.co.seoulit.his.adminservice.auth.dto.AuthDto;
import kr.co.seoulit.his.adminservice.auth.dto.AuthRequestDto;
import kr.co.seoulit.his.adminservice.auth.service.AuthService;
import kr.co.seoulit.his.adminservice.common.exception.BusinessException;
import kr.co.seoulit.his.adminservice.common.exception.ErrorCode;
import kr.co.seoulit.his.adminservice.common.response.ApiResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 로그인 REST API — /api/auth
 * 세션(HttpSession)에 사용자 저장, 프론트는 userInfo만 localStorage 저장
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // --- [로그인] POST /api/auth/login ---
    @PostMapping("/login")
    public ApiResponse<AuthDto> login(@RequestBody AuthRequestDto request, HttpSession session) {
        AuthDto user = authService.login(request);
        session.setAttribute(AuthService.SESSION_USER_KEY, user);
        return ApiResponse.success(user);
    }

    // --- [세션 확인] GET /api/auth/me ---
    @GetMapping("/me")
    public ApiResponse<AuthDto> me(HttpSession session) {
        AuthDto user = (AuthDto) session.getAttribute(AuthService.SESSION_USER_KEY);
        if (user == null) {
            throw new BusinessException(ErrorCode.AUTH_LOGIN_REQUIRED);
        }
        return ApiResponse.success(user);
    }

    // --- [로그아웃] POST /api/auth/logout ---
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session) {
        session.invalidate();
        return ApiResponse.success(null);
    }
}

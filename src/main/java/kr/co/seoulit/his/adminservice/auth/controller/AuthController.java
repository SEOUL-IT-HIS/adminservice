package kr.co.seoulit.his.adminservice.auth.controller;

import kr.co.seoulit.his.adminservice.auth.dto.AuthRequestDto;
import kr.co.seoulit.his.common.session.SessionUser;
import kr.co.seoulit.his.adminservice.auth.service.AuthService;
import kr.co.seoulit.his.adminservice.common.exception.BusinessException;
import kr.co.seoulit.his.adminservice.common.exception.ErrorCode;
import kr.co.seoulit.his.adminservice.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
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
    public ApiResponse<SessionUser> login(@RequestBody AuthRequestDto request,
                                          HttpServletRequest httpRequest,
                                          HttpSession session) {
        SessionUser user = authService.login(request);

        /*
         * 로그인에 성공한 "직후" 세션 ID(JSESSIONID)를 새로 발급한다. — 세션 고정(Session Fixation) 방어
         *
         * 이게 없으면 이런 공격이 가능하다.
         * 1) 공격자가 먼저 접속해 세션 ID 를 하나 받는다 (예: ABC)
         * 2) 그 ABC 를 피해자 브라우저에 심는다
         * 3) 피해자가 그 상태로 정상 로그인하면, 서버는 기존 세션 ABC 에 로그인 정보를 넣는다
         * 4) 공격자는 ABC 를 이미 알고 있으므로 비밀번호 없이 그대로 로그인된 상태가 된다
         *
         * ID 만 새로 발급되고 세션 객체와 그 안의 값은 그대로라, 아래 setAttribute 는 영향받지 않는다.
         * 인증에 성공한 뒤에 호출한다 — authService.login 이 실패하면 예외를 던져 여기까지 오지 않는다.
         */
        httpRequest.changeSessionId();

        session.setAttribute(AuthService.SESSION_USER_KEY, user);
        return ApiResponse.success(user);
    }

    // --- [세션 확인] GET /api/auth/me ---
    @GetMapping("/me")
    public ApiResponse<SessionUser> me(HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute(AuthService.SESSION_USER_KEY);
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

package kr.co.seoulit.his.adminservice.auth.controller;

import jakarta.validation.Valid;
import kr.co.seoulit.his.adminservice.auth.dto.LoginRequest;
import kr.co.seoulit.his.adminservice.auth.dto.LoginResponse;
import kr.co.seoulit.his.adminservice.auth.service.AuthService;
import kr.co.seoulit.his.adminservice.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
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
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }
}

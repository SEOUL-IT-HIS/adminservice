package kr.co.seoulit.his.adminservice.auth.service;

import kr.co.seoulit.his.adminservice.auth.dto.LoginRequest;
import kr.co.seoulit.his.adminservice.auth.dto.LoginResponse;

public interface AuthService {

    /**
     * 로그인 ID / 비밀번호 검증 후 계정·직원 정보를 반환한다.
     * HttpSession 저장은 AuthController 에서 수행한다.
     */
    LoginResponse login(LoginRequest request);
}

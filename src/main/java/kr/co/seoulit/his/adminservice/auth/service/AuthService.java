package kr.co.seoulit.his.adminservice.auth.service;

import kr.co.seoulit.his.adminservice.auth.dto.AuthDto;
import kr.co.seoulit.his.adminservice.auth.dto.AuthRequestDto;

public interface AuthService {

    /** HttpSession 에 저장할 사용자 키 */
    String SESSION_USER_KEY = "LOGIN_USER";

    AuthDto login(AuthRequestDto request);
}

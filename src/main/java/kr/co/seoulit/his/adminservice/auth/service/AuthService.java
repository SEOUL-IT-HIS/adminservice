package kr.co.seoulit.his.adminservice.auth.service;

import kr.co.seoulit.his.adminservice.auth.dto.AuthRequestDto;
import kr.co.seoulit.his.common.session.SessionUser;

public interface AuthService {

    /**
     * HttpSession 에 저장할 사용자 키.
     *
     * 세션이 Redis 에 저장되면 다른 서비스도 이 키로 값을 꺼낸다.
     * (Redis 에는 sessionAttr:LOGIN_USER 라는 이름으로 들어간다)
     * 바꾸면 다른 서비스가 못 찾으므로 팀 합의 없이 고치지 말 것.
     */
    String SESSION_USER_KEY = "LOGIN_USER";

    SessionUser login(AuthRequestDto request);
}

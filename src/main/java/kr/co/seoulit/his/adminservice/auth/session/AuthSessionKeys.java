package kr.co.seoulit.his.adminservice.auth.session;

/**
 * HttpSession 에 넣는 attribute 이름 상수.
 * session.setAttribute(키, 값) / getAttribute(키) 에 사용한다.
 */
public final class AuthSessionKeys {

    /** 로그인한 사용자 정보 (LoginResponse) */
    public static final String LOGIN_USER = "LOGIN_USER";

    private AuthSessionKeys() {
    }
}

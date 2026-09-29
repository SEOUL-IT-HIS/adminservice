package kr.co.seoulit.his.adminservice.auth.service;

import kr.co.seoulit.his.adminservice.auth.dto.AuthDto;
import kr.co.seoulit.his.adminservice.auth.dto.AuthRequestDto;
import kr.co.seoulit.his.common.session.SessionUser;

import java.util.List;

public interface AuthService {

    /**
     * HttpSession 에 저장할 사용자 키.
     *
     * 세션이 Redis 에 저장되면 다른 서비스도 이 키로 값을 꺼낸다.
     * (Redis 에는 sessionAttr:LOGIN_USER 라는 이름으로 들어간다)
     * 바꾸면 다른 서비스가 못 찾으므로 팀 합의 없이 고치지 말 것.
     */
    String SESSION_USER_KEY = "LOGIN_USER";

    /**
     * 초기 비밀번호. 직원 등록(EmpServiceImpl)과 관리자 초기화(resetPassword)가 같은 값을 쓴다.
     * DB 에는 이 값 그대로가 아니라 BCrypt 로 바꾼 값이 저장된다.
     */
    String DEFAULT_PASSWORD = "1111";

    SessionUser login(AuthRequestDto request);

    /** 본인 비밀번호 변경 — empId 는 세션의 로그인 사용자. request 의 currentPassword·newPassword 사용 */
    void changePassword(String empId, AuthRequestDto request);

    /** 전체 계정 목록 (잠금 시각·실패 횟수·역할 코드 포함, 잠긴 계정이 위). 비밀번호는 담지 않는다 */
    List<AuthDto> getAccountList();

    /** 잠긴 계정을 푼다 — LOCKED_AT 을 비우고 FAIL_COUNT 를 0 으로 */
    void unlockAccount(String empId);

    /** 관리자 비밀번호 초기화 — DEFAULT_PASSWORD 로 되돌리고 잠금도 푼다. 관리자(01) 계정은 대상 아님 (IH2-116) */
    void resetPassword(String empId);
}

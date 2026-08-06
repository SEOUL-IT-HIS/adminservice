package kr.co.seoulit.his.adminservice.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * admin-service 메시지 코드 (개발표준가이드 15.2)
 * 서비스 코드 ADM + 일련번호 3자리
 *
 * 담당 영역(auth/employee/commoncode/system) 초기화 — 재구현 시 코드 추가
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "ADM004", "요청 값이 올바르지 않습니다."),
    AUTH_LOGIN_FIELD_REQUIRED(HttpStatus.BAD_REQUEST, "ADM005", "아이디와 비밀번호를 입력하세요."),
    AUTH_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "ADM006", "아이디 또는 비밀번호가 올바르지 않습니다."),
    AUTH_ACCOUNT_LOCKED(HttpStatus.UNAUTHORIZED, "ADM007", "잠긴 계정입니다. 관리자에게 문의하세요.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

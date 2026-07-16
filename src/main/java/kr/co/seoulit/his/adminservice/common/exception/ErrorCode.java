package kr.co.seoulit.his.adminservice.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * admin-service 메시지 코드 (개발표준가이드 15.2)
 * 서비스 코드 ADM + 일련번호 3자리
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    EMP_NO_DUPLICATE(HttpStatus.CONFLICT, "ADM001", "이미 등록된 사번입니다."),
    LOGIN_ID_DUPLICATE(HttpStatus.CONFLICT, "ADM002", "이미 사용 중인 로그인 ID입니다."),
    EMP_NOT_FOUND(HttpStatus.NOT_FOUND, "ADM003", "직원 정보를 찾을 수 없습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "ADM004", "요청 값이 올바르지 않습니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "ADM005", "로그인 ID 또는 비밀번호가 올바르지 않습니다."),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "ADM006", "잠긴 계정입니다. 관리자에게 문의해주세요."),
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "ADM007", "비활성화된 계정입니다. 관리자에게 문의해주세요."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "ADM008", "로그인이 필요합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

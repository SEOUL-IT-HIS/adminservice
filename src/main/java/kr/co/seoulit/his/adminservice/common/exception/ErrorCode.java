package kr.co.seoulit.his.adminservice.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * admin-service 메시지 코드 (개발표준가이드 15.2)
 * 서비스 코드 ADM + 일련번호 3자리
 *
 * 담당 영역(auth/employee/commoncode/system) 초기화 — 재구현 시 코드 추가
 *
 * 아래 각 줄은 "이름(HTTP 상태코드, 코드문자열, 사용자에게 보여줄 메시지)" 하나씩을 뜻한다.
 * 예: BusinessException(ErrorCode.AUTH_ACCOUNT_LOCKED) 을 던지면
 * GlobalExceptionHandler가 이 줄의 세 값을 그대로 꺼내서 응답으로 만든다.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "ADM004", "요청 값이 올바르지 않습니다."),
    AUTH_LOGIN_FIELD_REQUIRED(HttpStatus.BAD_REQUEST, "ADM005", "아이디와 비밀번호를 입력하세요."),
    // 계정 미존재 / 비밀번호 불일치 / 휴직 상태를 전부 이 코드 하나로 묶어 쓴다 (AuthServiceImpl 참고 — 계정 존재 여부 비노출)
    AUTH_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "ADM006", "아이디 또는 비밀번호가 올바르지 않습니다."),
    AUTH_ACCOUNT_LOCKED(HttpStatus.UNAUTHORIZED, "ADM007", "잠긴 계정입니다. 관리자에게 문의하세요."),
    // 401(UNAUTHORIZED) 이어야 한다. 프론트 axios 가 "상태코드가 401이면 로그인 화면으로" 를 판단하는 기준이라,
    // 400 이면 세션이 끊겨도 프론트가 알아채지 못하고 화면에 에러 문구만 남는다.
    AUTH_LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "ADM008", "로그인이 필요합니다."),
    EMP_NOT_FOUND(HttpStatus.NOT_FOUND, "ADM009", "직원 정보를 찾을 수 없습니다."),
    INVALID_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "ADM010", "이미지 파일(jpg, png, webp)만 업로드할 수 있습니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ADM011", "이미지 업로드 중 오류가 발생했습니다."),
    COMMON_CODE_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "ADM012", "공통코드 그룹을 찾을 수 없습니다."),
    COMMON_CODE_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "ADM013", "공통코드 항목을 찾을 수 없습니다."),
    EMP_RRN_DUPLICATE(HttpStatus.CONFLICT, "ADM014", "이미 등록된 주민등록번호입니다."),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ADM015", "존재하지 않거나 비활성화된 역할입니다."),
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "ADM016", "메뉴를 찾을 수 없습니다."),
    // 예상하지 못한 서버 예외(NPE, DB 연결 실패 등)를 사용자에게 알릴 때 쓴다.
    // 원인(예외 클래스명·스택트레이스)은 서버 로그에만 남기고 화면에는 이 문장만 보여준다.
    // 개발표준가이드 15.1 — 시스템 메시지를 사용자 화면에 직접 노출하지 않는다.
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ADM017", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

package kr.co.seoulit.his.adminservice.common.exception;

import kr.co.seoulit.his.adminservice.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * [전역 예외 처리]
 * @RestControllerAdvice 가 붙은 클래스는 어디서도 직접 호출하지 않아도,
 * 이 프로젝트의 모든 @RestController 에서 아래 타입의 예외가 발생하면
 * Spring이 자동으로 여기로 가로채서 처리한다 (컨트롤러마다 try/catch를 안 써도 됨).
 * 예: 서비스 코드에서 throw new BusinessException(...) 하면, 이 클래스의
 * handleBusiness(...) 가 자동으로 실행되어 JSON 에러 응답을 만들어준다.
 *
 * 인터셉터(AuthSessionInterceptor)에서 던진 예외도 여기로 온다.
 * 그래서 로그인 안 된 요청의 401 응답도 다른 에러와 같은 모양의 JSON 이 된다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        // 주의: getCode()("ADM006" 같은 코드 문자열)가 아니라 getMessage()(실제 한글 문장)를 넣어야
        // 화면에 진짜 안내 메시지가 뜬다. 과거에 getCode()를 넣는 버그가 있었으니 되돌리지 말 것.
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.of(errorCode.getHttpStatus().value(), errorCode.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        // 검증 메시지가 비어 있을 때도 getCode()가 아니라 getMessage()를 쓴다.
        // 바로 위 handleBusiness 주석이 경고하는 것과 같은 실수 — getCode()를 넣으면
        // 사용자 화면에 "ADM004" 라는 코드 문자열이 그대로 표시된다.
        String message = detail.isBlank() ? ErrorCode.INVALID_REQUEST.getMessage() : detail;
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), message, null));
    }

    /**
     * 없는 주소로 요청했을 때 → 404
     *
     * 이 처리기가 없으면 아래 handleUnexpected(Exception) 가 잡아서 500 "서버 오류"로 나가고,
     * 서버 로그에도 "처리하지 못한 예외"가 에러로 쌓였다. 서버 잘못이 아니라 요청 주소가 틀린 것이다.
     * 스프링은 더 구체적인 예외 타입의 처리기를 먼저 고르므로, Exception 처리기보다 이쪽이 우선한다.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NoResourceFoundException ex) {
        ErrorCode errorCode = ErrorCode.NOT_FOUND;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.of(errorCode.getHttpStatus().value(), errorCode.getMessage(), null));
    }

    /**
     * 주소는 있는데 요청 방식이 틀렸을 때 (예: PUT 전용 주소에 GET) → 405
     * 위 handleNotFound 와 같은 이유로, 없으면 500 으로 잘못 나간다.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.of(errorCode.getHttpStatus().value(), errorCode.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex) {
        // 원인은 서버 로그에만 남긴다.
        // printStackTrace() 는 로그 레벨도 시각도 남지 않아 운영에서 추적이 안 된다.
        // 개발표준가이드 15.4 — 로그 출력은 공통 로거를 통해서만 한다.
        log.error("처리하지 못한 예외가 발생했습니다.", ex);

        // 화면에는 예외 클래스명과 원본 메시지를 내보내지 않는다.
        // 예전에는 "NullPointerException: Cannot invoke ...EmpEntity.getEmpId()" 처럼
        // 내부 패키지 구조가 사용자에게 그대로 노출됐다.
        // 개발표준가이드 15.1 — 시스템 메시지를 사용자 화면에 직접 노출하지 않는다.
        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.of(errorCode.getHttpStatus().value(), errorCode.getMessage(), null));
    }
}

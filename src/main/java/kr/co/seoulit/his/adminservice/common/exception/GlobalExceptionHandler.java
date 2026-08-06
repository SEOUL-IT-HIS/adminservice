package kr.co.seoulit.his.adminservice.common.exception;

import kr.co.seoulit.his.adminservice.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * [전역 예외 처리]
 * @RestControllerAdvice 가 붙은 클래스는 어디서도 직접 호출하지 않아도,
 * 이 프로젝트의 모든 @RestController 에서 아래 타입의 예외가 발생하면
 * Spring이 자동으로 여기로 가로채서 처리한다 (컨트롤러마다 try/catch를 안 써도 됨).
 * 예: 서비스 코드에서 throw new BusinessException(...) 하면, 이 클래스의
 * handleBusiness(...) 가 자동으로 실행되어 JSON 에러 응답을 만들어준다.
 */
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
        String message = detail.isBlank() ? ErrorCode.INVALID_REQUEST.getCode() : detail;
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), message, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex) {
        ex.printStackTrace();
        String detail = ex.getClass().getSimpleName() + ": " + ex.getMessage();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), detail, null));
    }
}

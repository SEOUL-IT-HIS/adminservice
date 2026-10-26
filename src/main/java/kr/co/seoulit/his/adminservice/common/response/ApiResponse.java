package kr.co.seoulit.his.adminservice.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 공통 API 응답 포맷 (개발표준가이드 11.3)
 * { "code": 200, "message": "SUCCESS", "data": {} }
 */
@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private final int code;
    private final String message;
    private final T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "SUCCESS", data);
    }

    public static <T> ApiResponse<T> of(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

}

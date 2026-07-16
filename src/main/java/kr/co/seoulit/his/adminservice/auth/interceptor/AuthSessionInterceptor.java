package kr.co.seoulit.his.adminservice.auth.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.seoulit.his.adminservice.auth.session.AuthSessionKeys;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * /api/** 요청에서 HttpSession 로그인 여부를 검사한다.
 * - 세션이 없거나 LOGIN_USER 가 없으면 401 + ADM008 을 반환한다.
 * - login / logout 은 WebMvcConfig 에서 exclude 한다.
 */
@Component
public class AuthSessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        // CORS preflight 는 세션 검사하지 않음
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // false: 세션이 없으면 새로 만들지 않음
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(AuthSessionKeys.LOGIN_USER) == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"ADM008\",\"data\":null}");
            return false;
        }

        return true;
    }
}

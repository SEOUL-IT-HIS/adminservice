package kr.co.seoulit.his.adminservice.common.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.seoulit.his.adminservice.auth.service.AuthService;
import kr.co.seoulit.his.adminservice.common.exception.BusinessException;
import kr.co.seoulit.his.adminservice.common.exception.ErrorCode;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * [로그인 세션 확인 인터셉터]
 *
 * 컨트롤러에 닿기 전에 "이 요청이 로그인한 사용자의 것인지"만 확인한다.
 *
 * 이게 없으면 화면(프론트 AppFrame)만 로그인을 요구하고 API 는 누구에게나 열려 있다.
 * 브라우저 주소창에 /api/emp/list 를 직접 치면 로그인한 적이 없어도 직원 명단이 그대로 나온다.
 * 화면을 잠그는 것과 데이터를 잠그는 것은 다른 일이라, 서버에서도 한 번 막아야 한다.
 *
 * 어느 경로에 적용할지(그리고 무엇을 예외로 둘지)는 AppConfig 에서 정한다.
 */
public class AuthSessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // getSession(false) 는 "세션이 없으면 새로 만들지 말고 그냥 null 을 달라"는 뜻이다.
        // 기본값(true)으로 두면 로그인하지 않은 요청이 들어올 때마다 빈 세션이 새로 생겨 서버에 쌓인다.
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(AuthService.SESSION_USER_KEY) == null) {
            // 여기서 던진 예외도 GlobalExceptionHandler 가 받아서
            // 다른 에러와 똑같은 모양의 JSON({code, message, data})으로 만들어 준다.
            throw new BusinessException(ErrorCode.AUTH_LOGIN_REQUIRED);
        }

        return true;   // 통과 — 컨트롤러로 넘어간다
    }
}

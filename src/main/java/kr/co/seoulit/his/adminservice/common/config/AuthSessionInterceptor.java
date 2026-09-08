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
 * 컨트롤러에 닿기 전에 "이 요청을 통과시켜도 되는지" 확인한다.
 *
 * 이게 없으면 화면(프론트 AppFrame)만 로그인을 요구하고 API 는 누구에게나 열려 있다.
 * 브라우저 주소창에 /api/emp/list 를 직접 치면 로그인한 적이 없어도 직원 명단이 그대로 나온다.
 * 화면을 잠그는 것과 데이터를 잠그는 것은 다른 일이라, 서버에서도 한 번 막아야 한다.
 *
 * 통과시키는 경우가 두 가지다. 부르는 쪽이 "사람"인지 "다른 서비스"인지에 따라 다르다.
 *  - 사람   : 브라우저가 로그인해서 받은 세션(JSESSIONID)이 살아 있는지 본다.
 *  - 서비스 : 팀이 나눠 가진 내부 키(X-Internal-Api-Key)가 맞는지 본다.
 *
 * 어느 경로에 적용할지(그리고 무엇을 예외로 둘지)는 AppConfig 에서 정한다.
 */
public class AuthSessionInterceptor implements HandlerInterceptor {

    /** 서비스 간 호출임을 알리는 헤더 이름 */
    public static final String INTERNAL_API_KEY_HEADER = "X-Internal-Api-Key";

    /** application.properties 의 msa.internal-api-key 값. AppConfig 가 넣어준다. */
    private final String internalApiKey;

    public AuthSessionInterceptor(String internalApiKey) {
        this.internalApiKey = internalApiKey;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        /*
         * [1] 다른 서비스가 부른 것인지 먼저 본다.
         *
         * 다른 MSA(patient-service 등)는 기동할 때 공통코드를 미리 받아두려고 admin-service 를
         * 직접 호출한다. 이건 브라우저가 아니라 자바 프로세스가 거는 호출이라 로그인한 사람이 없고,
         * 따라서 세션 쿠키도 존재할 수 없다. 세션만 보고 막으면 상대 서비스가 아예 기동하지 못한다
         * (기동 중 401 -> 예외 -> 프로세스 종료).
         *
         * 그래서 사람의 로그인 대신, 팀이 나눠 가진 키로 "우리 쪽 서비스가 맞다"를 확인한다.
         * 완벽한 인증은 아니다 — 설정 파일에 적어두는 공유 비밀이다. 다만 이 키를 모르는 외부
         * 호출은 막히므로, 아무나 부를 수 있던 상태보다는 분명히 낫다. 나중에 더 제대로 된
         * 방식으로 바꿀 때도 호출하는 쪽 코드는 그대로 두고 이 검사만 바꾸면 된다.
         *
         * 키가 비어 있으면(설정을 안 넣었으면) 이 검사는 아무도 통과시키지 않고
         * 아래 [2] 세션 검사로 넘어간다.
         */
        String requestKey = request.getHeader(INTERNAL_API_KEY_HEADER);
        if (internalApiKey != null && !internalApiKey.isBlank() && internalApiKey.equals(requestKey)) {
            return true;
        }

        /*
         * [2] 사람이 부른 것이므로 로그인 세션을 확인한다.
         *
         * getSession(false) 는 "세션이 없으면 새로 만들지 말고 그냥 null 을 달라"는 뜻이다.
         * 기본값(true)으로 두면 로그인하지 않은 요청이 들어올 때마다 빈 세션이 새로 생겨 서버에 쌓인다.
         */
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(AuthService.SESSION_USER_KEY) == null) {
            // 여기서 던진 예외도 GlobalExceptionHandler 가 받아서
            // 다른 에러와 똑같은 모양의 JSON({code, message, data})으로 만들어 준다.
            throw new BusinessException(ErrorCode.AUTH_LOGIN_REQUIRED);
        }

        return true;   // 통과 — 컨트롤러로 넘어간다
    }
}

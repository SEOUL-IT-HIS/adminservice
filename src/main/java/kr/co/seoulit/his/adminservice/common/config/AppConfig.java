package kr.co.seoulit.his.adminservice.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig {

    /**
     * 서비스 간 호출에 쓰는 공유 키. application.properties 의 msa.internal-api-key.
     * 콜론 뒤가 비어 있는 것(${...:})은 "설정이 없으면 빈 문자열로 두라"는 뜻이다.
     * 값이 없으면 인터셉터가 내부 호출을 아무것도 통과시키지 않는다(= 예전과 같은 동작).
     */
    @Value("${msa.internal-api-key:}")
    private String internalApiKey;

    /**
     * 팀 로컬/LAN Next.js 연동용 CORS + 로그인 세션 확인 인터셉터 등록.
     * - 각자 localhost:3000 에서 프론트 기동
     * - API는 MSA 기동 PC(예: 192.168.1.128:8080)로 호출
     * 운영에서는 Gateway / Nginx에서 처리하는 것을 권장한다.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOriginPatterns(
                                "http://localhost:3000",
                                "http://127.0.0.1:3000",
                                "http://192.168.*.*:3000"
                        )
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }

            /**
             * 로그인하지 않은 요청이 API 에 닿지 못하도록 막는다.
             *
             * 예외로 열어두는 세 가지 (막으면 로그인 자체가 불가능해진다):
             * - /api/auth/login  : 로그인하려면 당연히 로그인 전에 부를 수 있어야 한다
             * - /api/auth/logout : 이미 세션이 끊긴 상태에서도 로그아웃은 성공해야 한다
             *                      (막으면 로그아웃 버튼이 401 에러를 내뱉는다)
             * - /api/auth/me     : "나 로그인 되어 있나?" 를 물어보는 창구 자체다.
             *                      여기까지 막으면 프론트가 로그인 여부를 확인할 방법이 없어진다.
             *                      뚫린 문은 아니다 — AuthController.me() 가 스스로 세션을 검사한다.
             *
             * 아래 공통코드 조회 두 개는 [임시] 예외다.
             * 다른 서비스들이 기동할 때 이 두 API 를 부르는데, 세션 가드가 붙은 순간 401 을 받아
             * 서비스가 아예 뜨지 못했다. 각 팀이 X-Internal-Api-Key 헤더를 붙이는 작업을 마치면
             * 이 두 줄을 지운다. (조회(GET)만 열려 있고 /register, /update 는 계속 보호된다)
             */
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new AuthSessionInterceptor(internalApiKey))
                        .addPathPatterns("/api/**")
                        .excludePathPatterns(
                                "/api/auth/login",
                                "/api/auth/logout",
                                "/api/auth/me",
                                "/api/commonCodeGroup/list",
                                "/api/commonCodeItem/list"
                        );
            }
        };
    }
}

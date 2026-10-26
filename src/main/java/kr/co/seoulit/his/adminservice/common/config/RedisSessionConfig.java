package kr.co.seoulit.his.adminservice.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

@Configuration
public class RedisSessionConfig {

    /**
     * "@class" 에 적힌 클래스를 되살릴 때 허용할 범위.
     *
     * adminservice 가 아니라 his 로 잡은 이유 —
     * 세션에 담기는 SessionUser 가 kr.co.seoulit.his.common.session 에 있고,
     * 다른 서비스(billingservice 등)도 이 설정 파일을 그대로 복사해서 쓰기 때문이다.
     * 이 한 줄이면 어느 서비스에서든 고치지 않고 동작한다.
     */
    private static final String ALLOWED_PACKAGE = "kr.co.seoulit.his.";

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        // 우리 패키지만 허용한다. "@class" 에 적힌 클래스를 되살릴 때 이 목록에 없으면 거부한다.
        // java.lang. 을 열어두면 ProcessBuilder 같은 위험한 클래스까지 들어오므로 넣지 않는다.
        // (String 은 final 이라 애초에 "@class" 가 붙지 않아 허용할 필요도 없다)
        PolymorphicTypeValidator allowedTypes = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(ALLOWED_PACKAGE)
                .build();

        return GenericJacksonJsonRedisSerializer.builder()
                .customize(builder -> {
                    builder.activateDefaultTyping(
                            allowedTypes,
                            DefaultTyping.NON_FINAL, // EVERYTHING -> NON_FINAL로 수정
                            JsonTypeInfo.As.PROPERTY
                    );
                })
                .build();
    }
}
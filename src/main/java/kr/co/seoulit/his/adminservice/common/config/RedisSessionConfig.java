package kr.co.seoulit.his.adminservice.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * [세션을 Redis 에 저장할 때 쓰는 형식 설정]
 *
 * Redis 는 자바 객체를 그대로 담지 못하고 바이트만 저장한다.
 * 그래서 객체를 바이트로 바꾸는 "변환기"가 필요한데, 기본값은 자바 전용 방식이라
 * 읽는 쪽에도 똑같은 클래스가 있어야만 꺼낼 수 있다.
 * 그러면 다른 서비스(billing, patient ...)가 세션을 못 읽으므로 JSON 으로 바꿔서 저장한다.
 *
 * 빈 이름이 반드시 springSessionDefaultRedisSerializer 여야 한다.
 * Spring Session 이 이 이름으로 찾기 때문에, 이름을 바꾸면 기본 방식으로 돌아가 버린다.
 */
@Configuration
public class RedisSessionConfig {

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        /*
         * enableUnsafeDefaultTyping 은 JSON 에 "@class" 라는 항목을 같이 적어 둔다.
         * 이게 없으면 우리가 세션을 다시 읽을 때 SessionUser 가 아니라 Map 으로 돌아와서
         * (SessionUser) 형변환에서 오류가 난다.
         *
         * 이름에 unsafe 가 붙은 이유는 "@class 에 적힌 아무 클래스나 만들어낸다"는 뜻이라서다.
         * Redis 에 값을 써넣을 수 있는 사람이 있으면 위험할 수 있다.
         * 지금은 Redis 에 비밀번호(requirepass)가 걸려 있지 않으므로 반드시 함께 설정할 것.
         */
        return GenericJacksonJsonRedisSerializer.builder()
                .enableUnsafeDefaultTyping()
                .build();
    }
}

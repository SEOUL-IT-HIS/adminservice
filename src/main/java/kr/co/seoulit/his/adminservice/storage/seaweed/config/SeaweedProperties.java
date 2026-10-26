package kr.co.seoulit.his.adminservice.storage.seaweed.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "seaweed")
public class SeaweedProperties {

    /**
     * SeaweedFS S3 API 주소 (예: http://localhost:8333)
     *
     * 서버(admin-service)가 파일을 올리고 지울 때 쓰는 "내부용" 주소다.
     * 브라우저는 이 주소를 쓰지 않는다. publicBaseUrl 주석 참고.
     */
    private String endpoint;

    /**
     * 브라우저가 사진을 받아올 때 쓰는 "공개용" 주소.
     *
     * 직원 사진은 서버끼리 주고받는 게 아니라, 브라우저가 &lt;img src&gt; 로 직접 받아온다.
     * 그래서 브라우저가 닿을 수 있는 주소여야 하는데, 이게 내부용 endpoint 와 다를 수 있다.
     *
     * - 지금(개인 PC 환경): endpoint 와 같은 값. 브라우저가 8333 으로 직접 간다.
     * - 원격 서버 + Nginx 로 옮긴 뒤: "/files" 처럼 Nginx 가 대신 받아주는 경로.
     *   보안상 8333 을 방화벽에서 막게 되는데, 그러면 브라우저가 직접 못 가기 때문이다.
     *   (Nginx 에 location /files/ { proxy_pass http://seaweedfs:8333/; } 가 필요하다)
     *
     * 값을 안 적으면 endpoint 와 같은 값으로 채워진다. 즉 지금 동작은 그대로다.
     */
    private String publicBaseUrl;

    /** 업로드/조회할 버킷 이름 */
    private String bucket;

    /**
     * publicBaseUrl 이 비어 있으면 endpoint 를 대신 쓴다.
     *
     * 설정 파일에 한 줄 안 적었다고 사진이 깨지면 안 되므로, 예전과 같은 동작을 기본값으로 둔다.
     */
    public String getPublicBaseUrl() {
        if (publicBaseUrl == null || publicBaseUrl.isBlank()) {
            return endpoint;
        }
        return publicBaseUrl;
    }
}

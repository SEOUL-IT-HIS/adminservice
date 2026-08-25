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

    /** SeaweedFS S3 API 주소 (예: http://localhost:8333) */
    private String endpoint;

    /** 업로드/조회할 버킷 이름 */
    private String bucket;
}

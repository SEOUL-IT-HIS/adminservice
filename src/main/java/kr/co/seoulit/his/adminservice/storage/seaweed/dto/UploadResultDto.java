package kr.co.seoulit.his.adminservice.storage.seaweed.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadResultDto {

    /** S3 버킷 안에서의 파일 키(우리가 UUID로 직접 만든 이름) */
    private String fileName;

    /** 조회용 URL (endpoint + bucket + fileName 조합) */
    private String url;
}
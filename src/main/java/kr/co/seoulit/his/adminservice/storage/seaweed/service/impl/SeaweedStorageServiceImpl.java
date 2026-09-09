package kr.co.seoulit.his.adminservice.storage.seaweed.service.impl;

import kr.co.seoulit.his.adminservice.common.exception.BusinessException;
import kr.co.seoulit.his.adminservice.common.exception.ErrorCode;
import kr.co.seoulit.his.adminservice.storage.seaweed.config.SeaweedProperties;
import kr.co.seoulit.his.adminservice.storage.seaweed.dto.UploadResultDto;
import kr.co.seoulit.his.adminservice.storage.seaweed.service.SeaweedStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SeaweedStorageServiceImpl implements SeaweedStorageService {

    private final SeaweedProperties seaweedProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public UploadResultDto upload(MultipartFile file) {
        String fileName = UUID.randomUUID() + getExtension(file.getOriginalFilename());

        // 주소를 두 개 만든다. 하나는 서버가 올릴 때, 하나는 브라우저가 볼 때 쓴다.
        // (왜 나눠야 하는지는 SeaweedProperties 의 publicBaseUrl 주석에 적어뒀다)
        String uploadUrl = buildUrl(seaweedProperties.getEndpoint(), fileName);
        String publicUrl = buildUrl(seaweedProperties.getPublicBaseUrl(), fileName);

        HttpHeaders headers = new HttpHeaders();
        MediaType contentType = file.getContentType() != null
                ? MediaType.parseMediaType(file.getContentType())
                : MediaType.APPLICATION_OCTET_STREAM;
        headers.setContentType(contentType);

        try {
            restTemplate.put(uploadUrl, new HttpEntity<>(file.getBytes(), headers));
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }

        // DB(PROFILE_IMAGE_URL)에 저장되어 브라우저에게 그대로 내려가는 값이므로 공개용 주소를 넣는다.
        return new UploadResultDto(fileName, publicUrl);
    }

    /**
     * 파일 이름으로 지운다.
     *
     * 예전에는 DB 에 저장된 URL 을 그대로 받아서 지웠는데, 그 URL 이 공개용(예: /files/...)으로
     * 바뀌면 서버가 그 주소로는 SeaweedFS 에 닿지 못한다. 그래서 지울 때는 파일 이름만 받아
     * 내부용 endpoint 로 주소를 다시 만든다. 파일 이름은 PROFILE_IMAGE_FID 에 저장돼 있다.
     */
    @Override
    public void delete(String fileName) {
        restTemplate.delete(buildUrl(seaweedProperties.getEndpoint(), fileName));
    }

    /** 앞부분 주소 + 버킷 + 파일 이름 을 "/" 로 이어 붙인다. */
    private String buildUrl(String baseUrl, String fileName) {
        return baseUrl + "/" + seaweedProperties.getBucket() + "/" + fileName;
    }

    private String getExtension(String originalFileName) {
        if (originalFileName == null || !originalFileName.contains(".")) {
            return "";
        }
        return originalFileName.substring(originalFileName.lastIndexOf("."));
    }
}
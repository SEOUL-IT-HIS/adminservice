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
        String url = seaweedProperties.getEndpoint() + "/" + seaweedProperties.getBucket() + "/" + fileName;

        HttpHeaders headers = new HttpHeaders();
        MediaType contentType = file.getContentType() != null
                ? MediaType.parseMediaType(file.getContentType())
                : MediaType.APPLICATION_OCTET_STREAM;
        headers.setContentType(contentType);

        try {
            restTemplate.put(url, new HttpEntity<>(file.getBytes(), headers));
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }

        return new UploadResultDto(fileName, url);
    }

    @Override
    public void delete(String fileUrl) {
        restTemplate.delete(fileUrl);
    }

    private String getExtension(String originalFileName) {
        if (originalFileName == null || !originalFileName.contains(".")) {
            return "";
        }
        return originalFileName.substring(originalFileName.lastIndexOf("."));
    }
}
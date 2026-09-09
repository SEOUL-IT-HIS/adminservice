package kr.co.seoulit.his.adminservice.storage.seaweed.service;

import kr.co.seoulit.his.adminservice.storage.seaweed.dto.UploadResultDto;
import org.springframework.web.multipart.MultipartFile;

public interface SeaweedStorageService {

    UploadResultDto upload(MultipartFile file);

    /** 파일 이름(PROFILE_IMAGE_FID 에 저장된 값)으로 지운다. URL 이 아니다. */
    void delete(String fileName);
}

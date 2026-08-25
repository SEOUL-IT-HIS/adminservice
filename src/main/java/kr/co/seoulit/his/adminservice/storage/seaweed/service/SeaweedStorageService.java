package kr.co.seoulit.his.adminservice.storage.seaweed.service;

import kr.co.seoulit.his.adminservice.storage.seaweed.dto.UploadResultDto;
import org.springframework.web.multipart.MultipartFile;

public interface SeaweedStorageService {

    UploadResultDto upload(MultipartFile file);

    void delete(String fileUrl);
}

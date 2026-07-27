package kr.co.seoulit.his.adminservice.commonCode.service.impl;

import kr.co.seoulit.his.adminservice.commonCode.entity.CommonCodeItemEntity;
import kr.co.seoulit.his.adminservice.commonCode.repository.CommonCodeItemRepository;
import kr.co.seoulit.his.adminservice.commonCode.service.CommonCodeItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonCodeItemServiceImpl implements CommonCodeItemService {

    private final CommonCodeItemRepository commonCodeItemRepository;

    @Override
    public List<CommonCodeItemEntity> selectCommonCodeItemList(Long codeId) {
        return commonCodeItemRepository.findByGroupIdAndUseYnOrderByCodeIdAsc(codeId, "Y");
    }

}

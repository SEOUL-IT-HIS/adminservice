package kr.co.seoulit.his.adminservice.commonCode.service.impl;


import kr.co.seoulit.his.adminservice.commonCode.dto.CommonCodeItemDto;
import kr.co.seoulit.his.adminservice.commonCode.entity.CommonCodeItemEntity;
import kr.co.seoulit.his.adminservice.commonCode.mapper.CommonCodeItemMapper;
import kr.co.seoulit.his.adminservice.commonCode.repository.CommonCodeItemRepository;
import kr.co.seoulit.his.adminservice.commonCode.service.CommonCodeItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonCodeItemServiceImpl implements CommonCodeItemService {

    private final CommonCodeItemRepository commonCodeItemRepository;
    private final CommonCodeItemMapper commonCodeItemMapper;

    // ========== [목록] ==========
    @Override
    public List<CommonCodeItemEntity> selectCommonCodeItemList(Long codeId) {
        return commonCodeItemRepository.findByGroupIdAndUseYnOrderByCodeIdAsc(codeId, "Y");
    }

    // ========== [등록] ==========
    // 1) Mapper.toEntity(dto)  2) Repository.save(entity)
    @Override
    public CommonCodeItemEntity insertCommonCodeItem(CommonCodeItemDto dto) {
        CommonCodeItemEntity entity = commonCodeItemMapper.toItemEntity(dto);
        return commonCodeItemRepository.save(entity);
    }

}

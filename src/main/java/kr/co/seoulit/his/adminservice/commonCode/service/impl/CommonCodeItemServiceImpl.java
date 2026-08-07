package kr.co.seoulit.his.adminservice.commonCode.service.impl;

import kr.co.seoulit.his.adminservice.commonCode.dto.CommonCodeItemDto;
import kr.co.seoulit.his.adminservice.commonCode.entity.CommonCodeItemEntity;
import kr.co.seoulit.his.adminservice.commonCode.mapper.CommonCodeItemMapper;
import kr.co.seoulit.his.adminservice.commonCode.repository.CommonCodeItemRepository;
import kr.co.seoulit.his.adminservice.commonCode.service.CommonCodeItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommonCodeItemServiceImpl implements CommonCodeItemService {

    private final CommonCodeItemRepository commonCodeItemRepository;
    private final CommonCodeItemMapper commonCodeItemMapper;

    // ========== [목록] ==========
    @Override
    public List<CommonCodeItemEntity> selectCommonCodeItemList(String groupId) {
        return commonCodeItemRepository.findByGroupIdOrderBySortOrderAsc(groupId);
    }

    // ========== [등록] ==========
    // 1) Mapper.toEntity(dto)  2) Repository.save(entity)
    @Override
    public CommonCodeItemEntity insertCommonCodeItem(CommonCodeItemDto dto) {
        CommonCodeItemEntity entity = commonCodeItemMapper.toItemEntity(dto);
        return commonCodeItemRepository.save(entity);
    }

    // ========== [수정] ==========
    @Override
    public CommonCodeItemEntity updateCommonCodeItem(String codeId, CommonCodeItemDto dto) {
        CommonCodeItemEntity entity = commonCodeItemRepository.findById(codeId).orElseThrow(() -> new IllegalArgumentException("Invalid code ID"));
        entity.setCodeName(dto.getCodeName());
        entity.setUseYn(dto.getUseYn());
        return commonCodeItemRepository.save(entity);
    }

}

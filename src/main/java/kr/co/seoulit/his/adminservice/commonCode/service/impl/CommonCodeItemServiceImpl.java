package kr.co.seoulit.his.adminservice.commonCode.service.impl;

import kr.co.seoulit.his.adminservice.commonCode.dto.CommonCodeItemDto;
import kr.co.seoulit.his.adminservice.commonCode.entity.CommonCodeItemEntity;
import kr.co.seoulit.his.adminservice.commonCode.mapper.CommonCodeItemMapper;
import kr.co.seoulit.his.adminservice.commonCode.repository.CommonCodeItemRepository;
import kr.co.seoulit.his.adminservice.commonCode.service.CommonCodeItemService;
import kr.co.seoulit.his.adminservice.common.exception.BusinessException;
import kr.co.seoulit.his.adminservice.common.exception.ErrorCode;
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
        // 화면에서 정렬순서를 입력받지 않으므로 서버가 그룹 내 마지막 번호 다음으로 정해준다
        entity.setSortOrder(nextSortOrder(dto.getGroupId()));
        return commonCodeItemRepository.save(entity);
    }

    // ========== [수정] ==========
    @Override
    public CommonCodeItemEntity updateCommonCodeItem(String codeId, CommonCodeItemDto dto) {
        CommonCodeItemEntity entity = commonCodeItemRepository.findById(codeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMON_CODE_ITEM_NOT_FOUND));
        entity.setCodeName(dto.getCodeName());
        entity.setUseYn(dto.getUseYn());
        return commonCodeItemRepository.save(entity);
    }

    // 같은 그룹에서 가장 큰 정렬순서 + 1 을 돌려준다. 항목이 하나도 없으면 1 부터 시작한다.
    private Integer nextSortOrder(String groupId) {
        List<CommonCodeItemEntity> items = commonCodeItemRepository.findByGroupIdOrderBySortOrderAsc(groupId);
        int max = 0;
        for (CommonCodeItemEntity item : items) {
            if (item.getSortOrder() != null && item.getSortOrder() > max) {
                max = item.getSortOrder();
            }
        }
        return max + 1;
    }

}

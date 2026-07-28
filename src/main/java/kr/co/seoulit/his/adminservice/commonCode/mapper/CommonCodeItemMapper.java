package kr.co.seoulit.his.adminservice.commonCode.mapper;

import kr.co.seoulit.his.adminservice.commonCode.dto.CommonCodeItemDto;
import kr.co.seoulit.his.adminservice.commonCode.entity.CommonCodeItemEntity;
import org.springframework.stereotype.Component;

/**
 * [Mapper] 공통코드 아이템 — DTO ↔ Entity 변환
 * - toItemEntity: 등록 시 DTO → Entity (codeId는 DB SEQUENCE로 생성)
 */
@Component
public class CommonCodeItemMapper {

    // ========== [등록용] DTO → Entity ==========
    public CommonCodeItemEntity toItemEntity(CommonCodeItemDto dto) {
        CommonCodeItemEntity itemEntity = new CommonCodeItemEntity();
        itemEntity.setGroupId(dto.getGroupId());
        itemEntity.setCodeValue(dto.getCodeValue());
        itemEntity.setCodeName(dto.getCodeName());
        itemEntity.setUseYn(dto.getUseYn());
        return itemEntity;
    }

}

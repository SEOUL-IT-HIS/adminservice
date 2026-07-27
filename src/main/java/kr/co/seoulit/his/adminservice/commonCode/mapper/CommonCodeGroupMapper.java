package kr.co.seoulit.his.adminservice.commonCode.mapper;

import kr.co.seoulit.his.adminservice.commonCode.dto.CommonCodeGroupDto;
import kr.co.seoulit.his.adminservice.commonCode.entity.CommonCodeGroupEntity;
import org.springframework.stereotype.Component;

/**
 * [Mapper] 공통코드 그룹 — DTO ↔ Entity 변환
 * - toEntity: 등록 시 DTO → Entity (groupId는 DB IDENTITY로 생성)
 */
@Component
public class CommonCodeGroupMapper {

    // ========== [등록용] DTO → Entity ==========
    public CommonCodeGroupEntity toEntity(CommonCodeGroupDto dto) {
        CommonCodeGroupEntity entity = new CommonCodeGroupEntity();
        entity.setGroupCode(dto.getGroupCode());
        entity.setGroupName(dto.getGroupName());
        entity.setUseYn(dto.getUseYn());
        return entity;
    }

}

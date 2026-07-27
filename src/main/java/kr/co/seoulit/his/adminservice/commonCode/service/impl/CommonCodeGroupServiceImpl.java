package kr.co.seoulit.his.adminservice.commonCode.service.impl;

import kr.co.seoulit.his.adminservice.commonCode.dto.CommonCodeGroupDto;
import kr.co.seoulit.his.adminservice.commonCode.entity.CommonCodeGroupEntity;
import kr.co.seoulit.his.adminservice.commonCode.mapper.CommonCodeGroupMapper;
import kr.co.seoulit.his.adminservice.commonCode.repository.CommonCodeGroupRepository;
import kr.co.seoulit.his.adminservice.commonCode.service.CommonCodeGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * [ServiceImpl] 공통코드 그룹 — 실제 비즈니스 로직
 * - Mapper: DTO → Entity 변환
 * - Repository: DB 저장/조회
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CommonCodeGroupServiceImpl implements CommonCodeGroupService {

    private final CommonCodeGroupRepository commonCodeGroupRepository;
    private final CommonCodeGroupMapper commonCodeGroupMapper;

    // ========== [목록] ==========
    @Override
    public List<CommonCodeGroupEntity> selectCommonCodeGroupList() {
        return commonCodeGroupRepository.findByUseYnOrderByGroupIdAsc("Y");
    }

    // ========== [등록] ==========
    // 1) Mapper.toEntity(dto)  2) Repository.save(entity)
    @Override
    public CommonCodeGroupEntity insertCommonCodeGroup(CommonCodeGroupDto dto) {
        CommonCodeGroupEntity entity = commonCodeGroupMapper.toEntity(dto);
        return commonCodeGroupRepository.save(entity);
    }

}

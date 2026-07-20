package kr.co.seoulit.his.adminservice.commoncode.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.seoulit.his.adminservice.common.exception.BusinessException;
import kr.co.seoulit.his.adminservice.common.exception.ErrorCode;
import kr.co.seoulit.his.adminservice.commoncode.dto.CommonCodeGroupResponse;
import kr.co.seoulit.his.adminservice.commoncode.dto.CreateCommonCodeGroupRequest;
import kr.co.seoulit.his.adminservice.commoncode.dto.UpdateCommonCodeGroupRequest;
import kr.co.seoulit.his.adminservice.commoncode.entity.CommonCodeGroup;
import kr.co.seoulit.his.adminservice.commoncode.repository.CommonCodeGroupRepository;
import kr.co.seoulit.his.adminservice.commoncode.repository.CommonCodeRepository;
import kr.co.seoulit.his.adminservice.commoncode.service.CommonCodeGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonCodeGroupServiceImpl implements CommonCodeGroupService {

    private final CommonCodeGroupRepository commonCodeGroupRepository;
    private final CommonCodeRepository commonCodeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<CommonCodeGroupResponse> findAll() {
        return commonCodeGroupRepository.findAllByOrderByGroupCodeAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CommonCodeGroupResponse findById(Long groupId) {
        CommonCodeGroup group = commonCodeGroupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CODE_GROUP_NOT_FOUND));
        return toResponse(group);
    }

    @Override
    @Transactional
    public CommonCodeGroupResponse create(CreateCommonCodeGroupRequest request) {
        String groupCode = request.getGroupCode() == null ? "" : request.getGroupCode().trim();
        String groupName = request.getGroupName() == null ? "" : request.getGroupName().trim();

        if (commonCodeGroupRepository.existsByGroupCode(groupCode)) {
            throw new BusinessException(ErrorCode.CODE_GROUP_DUPLICATE);
        }

        CommonCodeGroup group = new CommonCodeGroup();
        group.setGroupId(nextSeq("COMMON_CODE_GROUP_SEQ"));
        group.setGroupCode(groupCode);
        group.setGroupName(groupName);
        group.setUseYn(StringUtils.hasText(request.getUseYn()) ? request.getUseYn().trim() : "Y");

        return toResponse(commonCodeGroupRepository.saveAndFlush(group));
    }

    @Override
    @Transactional
    public CommonCodeGroupResponse update(Long groupId, UpdateCommonCodeGroupRequest request) {
        CommonCodeGroup group = commonCodeGroupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CODE_GROUP_NOT_FOUND));

        if (request.getGroupName() != null) {
            group.setGroupName(request.getGroupName().trim());
        }
        if (request.getUseYn() != null) {
            group.setUseYn(request.getUseYn().trim());
        }

        return toResponse(commonCodeGroupRepository.saveAndFlush(group));
    }

    @Override
    @Transactional
    public void delete(Long groupId) {
        CommonCodeGroup group = commonCodeGroupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CODE_GROUP_NOT_FOUND));

        if (commonCodeRepository.existsByGroupId(groupId)) {
            throw new BusinessException(ErrorCode.CODE_GROUP_IN_USE);
        }

        commonCodeGroupRepository.delete(group);
    }

    private Long nextSeq(String sequenceName) {
        Object value = entityManager
                .createNativeQuery("SELECT " + sequenceName + ".NEXTVAL FROM DUAL")
                .getSingleResult();
        return ((Number) value).longValue();
    }

    private CommonCodeGroupResponse toResponse(CommonCodeGroup group) {
        return CommonCodeGroupResponse.builder()
                .groupId(group.getGroupId())
                .groupCode(group.getGroupCode())
                .groupName(group.getGroupName())
                .useYn(group.getUseYn())
                .build();
    }
}

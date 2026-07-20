package kr.co.seoulit.his.adminservice.commoncode.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.seoulit.his.adminservice.common.exception.BusinessException;
import kr.co.seoulit.his.adminservice.common.exception.ErrorCode;
import kr.co.seoulit.his.adminservice.commoncode.dto.CommonCodeResponse;
import kr.co.seoulit.his.adminservice.commoncode.dto.CreateCommonCodeRequest;
import kr.co.seoulit.his.adminservice.commoncode.dto.UpdateCommonCodeRequest;
import kr.co.seoulit.his.adminservice.commoncode.entity.CommonCode;
import kr.co.seoulit.his.adminservice.commoncode.entity.CommonCodeGroup;
import kr.co.seoulit.his.adminservice.commoncode.repository.CommonCodeGroupRepository;
import kr.co.seoulit.his.adminservice.commoncode.repository.CommonCodeRepository;
import kr.co.seoulit.his.adminservice.commoncode.service.CommonCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CommonCodeServiceImpl implements CommonCodeService {

    private final CommonCodeGroupRepository commonCodeGroupRepository;
    private final CommonCodeRepository commonCodeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<CommonCodeResponse> findByGroupCode(String groupCode, String useYn, String keyword) {
        if (!StringUtils.hasText(groupCode)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        CommonCodeGroup group = commonCodeGroupRepository.findByGroupCode(groupCode.trim())
                .orElseThrow(() -> new BusinessException(ErrorCode.CODE_GROUP_NOT_FOUND));

        List<CommonCode> codes;
        if (StringUtils.hasText(useYn)) {
            codes = commonCodeRepository.findByGroupIdAndUseYnOrderBySortOrderAscCodeValueAsc(
                    group.getGroupId(), useYn.trim());
        } else {
            codes = commonCodeRepository.findByGroupIdOrderBySortOrderAscCodeValueAsc(group.getGroupId());
        }

        if (StringUtils.hasText(keyword)) {
            String key = keyword.trim().toLowerCase(Locale.ROOT);
            codes = codes.stream()
                    .filter(code -> containsIgnoreCase(code.getCodeValue(), key)
                            || containsIgnoreCase(code.getCodeName(), key))
                    .toList();
        }

        return codes.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CommonCodeResponse findById(Long codeId) {
        CommonCode code = commonCodeRepository.findById(codeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CODE_NOT_FOUND));
        return toResponse(code);
    }

    @Override
    @Transactional
    public CommonCodeResponse create(CreateCommonCodeRequest request) {
        if (request.getGroupId() == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        CommonCodeGroup group = commonCodeGroupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CODE_GROUP_NOT_FOUND));

        String codeValue = request.getCodeValue() == null ? "" : request.getCodeValue().trim();
        if (commonCodeRepository.existsByGroupIdAndCodeValue(group.getGroupId(), codeValue)) {
            throw new BusinessException(ErrorCode.CODE_DUPLICATE);
        }

        CommonCode code = new CommonCode();
        code.setCodeId(nextSeq("COMMON_CODE_SEQ"));
        code.setGroupId(group.getGroupId());
        code.setParentCodeId(request.getParentCodeId());
        code.setCodeValue(codeValue);
        code.setCodeName(request.getCodeName() == null ? "" : request.getCodeName().trim());
        code.setSortOrder(request.getSortOrder());
        code.setUseYn(StringUtils.hasText(request.getUseYn()) ? request.getUseYn().trim() : "Y");

        return toResponse(commonCodeRepository.saveAndFlush(code));
    }

    @Override
    @Transactional
    public CommonCodeResponse update(Long codeId, UpdateCommonCodeRequest request) {
        CommonCode code = commonCodeRepository.findById(codeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CODE_NOT_FOUND));

        if (request.getCodeValue() != null) {
            String codeValue = request.getCodeValue().trim();
            if (commonCodeRepository.existsByGroupIdAndCodeValueAndCodeIdNot(
                    code.getGroupId(), codeValue, codeId)) {
                throw new BusinessException(ErrorCode.CODE_DUPLICATE);
            }
            code.setCodeValue(codeValue);
        }
        if (request.getCodeName() != null) {
            code.setCodeName(request.getCodeName().trim());
        }
        if (request.getSortOrder() != null) {
            code.setSortOrder(request.getSortOrder());
        }
        if (request.getUseYn() != null) {
            code.setUseYn(request.getUseYn().trim());
        }
        if (request.getParentCodeId() != null) {
            code.setParentCodeId(request.getParentCodeId());
        }

        return toResponse(commonCodeRepository.saveAndFlush(code));
    }

    @Override
    @Transactional
    public void delete(Long codeId) {
        CommonCode code = commonCodeRepository.findById(codeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CODE_NOT_FOUND));
        commonCodeRepository.delete(code);
    }

    private Long nextSeq(String sequenceName) {
        Object value = entityManager
                .createNativeQuery("SELECT " + sequenceName + ".NEXTVAL FROM DUAL")
                .getSingleResult();
        return ((Number) value).longValue();
    }

    private CommonCodeResponse toResponse(CommonCode code) {
        return CommonCodeResponse.builder()
                .codeId(code.getCodeId())
                .groupId(code.getGroupId())
                .parentCodeId(code.getParentCodeId())
                .codeValue(code.getCodeValue())
                .codeName(code.getCodeName())
                .sortOrder(code.getSortOrder())
                .useYn(code.getUseYn())
                .build();
    }

    private static boolean containsIgnoreCase(String value, String keywordLower) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keywordLower);
    }
}

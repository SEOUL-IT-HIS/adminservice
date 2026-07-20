package kr.co.seoulit.his.adminservice.commoncode.service;

import kr.co.seoulit.his.adminservice.commoncode.dto.CommonCodeResponse;
import kr.co.seoulit.his.adminservice.commoncode.dto.CreateCommonCodeRequest;
import kr.co.seoulit.his.adminservice.commoncode.dto.UpdateCommonCodeRequest;

import java.util.List;

public interface CommonCodeService {

    /**
     * 그룹코드로 항목 목록 조회
     * @param groupCode 업무 키
     * @param useYn Y/N/null(전체)
     * @param keyword 코드값/코드명 검색 (null이면 전체)
     */
    List<CommonCodeResponse> findByGroupCode(String groupCode, String useYn, String keyword);

    CommonCodeResponse findById(Long codeId);

    CommonCodeResponse create(CreateCommonCodeRequest request);

    CommonCodeResponse update(Long codeId, UpdateCommonCodeRequest request);

    void delete(Long codeId);
}

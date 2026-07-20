package kr.co.seoulit.his.adminservice.commoncode.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 공통코드 그룹 조회/목록 응답
 * - hisfrontend CommonCodeGroup 타입과 동일 계약
 */
@Getter
@Builder
public class CommonCodeGroupResponse {

    private Long groupId;
    private String groupCode;
    private String groupName;
    private String useYn;
}

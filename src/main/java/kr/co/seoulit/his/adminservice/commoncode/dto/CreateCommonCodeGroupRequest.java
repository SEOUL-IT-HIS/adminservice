package kr.co.seoulit.his.adminservice.commoncode.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 공통코드 그룹 등록 요청
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateCommonCodeGroupRequest {

    private String groupCode;
    private String groupName;
    /** Y/N. 미전달 시 Y */
    private String useYn;
}

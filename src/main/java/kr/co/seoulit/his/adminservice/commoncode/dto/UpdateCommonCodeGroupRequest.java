package kr.co.seoulit.his.adminservice.commoncode.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 공통코드 그룹 수정 요청
 * - groupCode 수정 불가 (업무 키)
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateCommonCodeGroupRequest {

    private String groupName;
    private String useYn;
}

package kr.co.seoulit.his.adminservice.commoncode.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 공통코드 항목 응답
 * - Select / 관리 화면 공용
 */
@Getter
@Builder
public class CommonCodeResponse {

    private Long codeId;
    private Long groupId;
    private Long parentCodeId;
    private String codeValue;
    private String codeName;
    private Integer sortOrder;
    private String useYn;
}

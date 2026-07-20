package kr.co.seoulit.his.adminservice.commoncode.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 공통코드 항목 등록 요청
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateCommonCodeRequest {

    private Long groupId;
    private Long parentCodeId;
    private String codeValue;
    private String codeName;
    private Integer sortOrder;
    /** Y/N. 미전달 시 Y */
    private String useYn;
}

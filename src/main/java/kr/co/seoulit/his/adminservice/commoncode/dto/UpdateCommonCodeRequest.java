package kr.co.seoulit.his.adminservice.commoncode.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 공통코드 항목 수정 요청
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateCommonCodeRequest {

    private Long parentCodeId;
    private String codeValue;
    private String codeName;
    private Integer sortOrder;
    private String useYn;
}

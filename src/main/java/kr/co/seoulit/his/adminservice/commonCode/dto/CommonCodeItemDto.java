package kr.co.seoulit.his.adminservice.commonCode.dto;

import lombok.*;

/**
 * [DTO] 공통코드 항목 — API 요청/응답용 객체
 * - Controller @RequestBody 로 받음
 */
@Getter
@Setter
@NoArgsConstructor
public class CommonCodeItemDto {

    private Long codeId;
    private Long groupId;
    private String codeValue;
    private String codeName;
    private String useYn;
}

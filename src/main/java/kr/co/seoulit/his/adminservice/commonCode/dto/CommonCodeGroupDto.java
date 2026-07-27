package kr.co.seoulit.his.adminservice.commonCode.dto;

import lombok.*;

/**
 * [DTO] 공통코드 그룹 — API 요청/응답용 객체
 * - Controller @RequestBody 로 받음
 */
@Getter
@Setter
@NoArgsConstructor
public class CommonCodeGroupDto {

    private Long groupId;
    private String groupCode;
    private String groupName;
    private String useYn;
}

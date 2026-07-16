package kr.co.seoulit.his.adminservice.system.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 메뉴 목록 응답 (flat)
 * - 트리 조립은 hisfrontend 에서 parentMenuId 기준으로 수행
 */
@Getter
@Builder
public class MenuResponse {

    private Long menuId;
    private Long parentMenuId;
    private String menuCode;
    private String menuName;
    private String menuUrl;
    private Integer sortOrder;
    private String useYn;
    private String areaKey;
    private String serviceCode;
}

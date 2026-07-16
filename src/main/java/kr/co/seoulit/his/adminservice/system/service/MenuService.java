package kr.co.seoulit.his.adminservice.system.service;

import kr.co.seoulit.his.adminservice.system.dto.MenuResponse;

import java.util.List;

public interface MenuService {

    /**
     * 사용 중인 메뉴 flat 목록
     * - USE_YN = 'Y'
     * - SORT_ORDER 오름차순
     * - 트리 조립 없음
     */
    List<MenuResponse> findAllActive();
}

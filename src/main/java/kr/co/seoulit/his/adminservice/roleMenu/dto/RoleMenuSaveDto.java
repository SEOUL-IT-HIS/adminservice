package kr.co.seoulit.his.adminservice.roleMenu.dto;

import lombok.Data;

import java.util.List;

/**
 * [권한 페이지 저장 요청]
 *
 * 화면에서 체크된 메뉴 ID 만 담아서 보낸다.
 *
 * 주의: 이 목록은 "추가할 것"이 아니라 "저장 후의 최종 상태"다.
 * 여기 없는 메뉴는 기존에 권한이 있었더라도 해제된다.
 * (체크박스 화면이라 최종 상태를 통째로 보내는 게 자연스럽고,
 *  토글마다 요청을 쏘면 중간에 끊겼을 때 상태가 어정쩡해진다)
 */
@Data
public class RoleMenuSaveDto {

    private List<String> menuIds;
}
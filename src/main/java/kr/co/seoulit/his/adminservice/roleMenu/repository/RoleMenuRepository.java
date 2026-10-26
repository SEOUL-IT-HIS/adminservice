package kr.co.seoulit.his.adminservice.roleMenu.repository;

import kr.co.seoulit.his.adminservice.roleMenu.entity.RoleMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * ROLE_MENU 조회/저장.
 *
 * 메서드 이름만 규칙대로 지으면 Spring Data 가 SQL 을 만들어준다.
 * EmpRole 은 diff 삭제 때문에 MyBatis 매퍼를 따로 뒀지만,
 * 여기서는 deleteByRoleIdAndMenuIdIn 하나로 해결돼서 XML 이 필요 없다.
 */
public interface RoleMenuRepository extends JpaRepository<RoleMenuEntity, String> {

    /** 역할 하나에 배정된 메뉴 권한 전부 */
    List<RoleMenuEntity> findByRoleId(String roleId);

    /** 이 역할의 행 중, menuIds 에 들어 있는 메뉴만 삭제 (권한 해제분) */
    void deleteByRoleIdAndMenuIdIn(String roleId, Collection<String> menuIds);
}
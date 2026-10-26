package kr.co.seoulit.his.adminservice.emp.mybatis;

import java.util.Collection;

import kr.co.seoulit.his.adminservice.emp.entity.EmpRoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * emp_role 테이블 전용 MyBatis 매퍼.
 * JPA(EmpRoleRepository)로 전체 삭제 후 재삽입하던 것을, diff(변경분)만 delete/insert 하도록
 * SQL을 직접 다루기 위해 도입했다. 실제 SQL은 emp/mybatis/EmpRoleMapper.xml 에 있다.
 */
@Mapper
public interface EmpRoleMapper {

    /** roleIds 에 있는 것 중 이 직원(empId)에게 배정된 행만 삭제 (역할 제거분) */
    void deleteByEmpIdAndRoleIds(@Param("empId") String empId, @Param("roleIds") Collection<String> roleIds);

    /** 역할 하나 추가 배정 (역할 추가분) */
    void insert(EmpRoleEntity empRole);
}

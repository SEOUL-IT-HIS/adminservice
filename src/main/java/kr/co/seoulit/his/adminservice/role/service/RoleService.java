package kr.co.seoulit.his.adminservice.role.service;

import kr.co.seoulit.his.adminservice.role.entity.RoleEntity;

import java.util.List;

public interface RoleService {
    List<RoleEntity> selectRoleList();
}

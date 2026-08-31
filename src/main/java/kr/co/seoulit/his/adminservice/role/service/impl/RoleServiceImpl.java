package kr.co.seoulit.his.adminservice.role.service.impl;

import kr.co.seoulit.his.adminservice.role.entity.RoleEntity;
import kr.co.seoulit.his.adminservice.role.repository.RoleRepository;
import kr.co.seoulit.his.adminservice.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<RoleEntity> selectRoleList() {
        return roleRepository.findByUseYn("Y");
    }
}

package kr.co.seoulit.his.adminservice.emp.service.impl;

import java.util.List;

import kr.co.seoulit.his.adminservice.emp.entity.EmpEntity;
import kr.co.seoulit.his.adminservice.emp.repository.EmpRepository;
import kr.co.seoulit.his.adminservice.emp.service.EmpService;
import kr.co.seoulit.his.adminservice.emp.dto.EmpDto;
import kr.co.seoulit.his.adminservice.emp.mapper.EmpMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EmpServiceImpl implements EmpService {

    private final EmpMapper empMapper;
    private final EmpRepository empRepository;

    // ========== [목록] ==========
    @Override
    public List<EmpEntity> selectEmpList() {
        return empRepository.findAll();
    }

    // ========== [등록] ==========
    @Override
    public EmpEntity createEmp(EmpDto dto) {
        EmpEntity empEntity = empMapper.toEmpEntity(dto);
        return empRepository.save(empEntity);
    }

    // ========== [수정] ==========
    @Override
    public EmpEntity updateEmp(Long empId, EmpDto dto) {
        EmpEntity empEntity = empRepository.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Emp ID"));
        empEntity.setEmpName(dto.getEmpName());
        empEntity.setEmpEmail(dto.getEmpEmail());
        empEntity.setEmpPhone(dto.getEmpPhone());
        empEntity.setRetireDate(dto.getRetireDate());
        empEntity.setEmpStatus(dto.getEmpStatus());
        empEntity.setDeptCode(dto.getDeptCode());
        return empRepository.save(empEntity);
    }

    // ========== [상세] ==========
    @Override
    public EmpEntity getEmpById(Long empId) {
        return empRepository.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Emp ID"));
    }
}

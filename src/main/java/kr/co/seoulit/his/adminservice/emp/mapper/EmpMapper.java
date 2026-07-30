package kr.co.seoulit.his.adminservice.emp.mapper;

import kr.co.seoulit.his.adminservice.emp.dto.EmpDto;
import kr.co.seoulit.his.adminservice.emp.entity.EmpEntity;
import org.springframework.stereotype.Component;

@Component
public class EmpMapper {

    // ========== [등록용] DTO → Entity ==========
    public EmpEntity toEmpEntity(EmpDto dto) {
        EmpEntity empEntity = new EmpEntity();
        empEntity.setEmpNo(dto.getEmpNo());
        empEntity.setEmpName(dto.getEmpName());
        empEntity.setEmpEmail(dto.getEmpEmail());
        empEntity.setEmpPhone(dto.getEmpPhone());
        empEntity.setHireDate(dto.getHireDate());
        empEntity.setDeptCode(dto.getDeptCode());
        return empEntity;
    }
}

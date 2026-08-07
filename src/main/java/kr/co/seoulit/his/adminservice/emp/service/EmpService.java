package kr.co.seoulit.his.adminservice.emp.service;


import java.util.List;
import kr.co.seoulit.his.adminservice.emp.dto.EmpDto;
import kr.co.seoulit.his.adminservice.emp.entity.EmpEntity;

public interface EmpService {

    List<EmpEntity> selectEmpList();

    EmpEntity getEmpById(String empId);

    EmpEntity createEmp(EmpDto emp);

    EmpEntity updateEmp(String empId, EmpDto dto);


}

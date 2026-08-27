package kr.co.seoulit.his.adminservice.emp.service;


import java.util.List;
import kr.co.seoulit.his.adminservice.emp.dto.EmpDto;
import kr.co.seoulit.his.adminservice.emp.dto.RrnCheckResultDto;
import kr.co.seoulit.his.adminservice.emp.entity.EmpEntity;
import org.springframework.web.multipart.MultipartFile;

public interface EmpService {

    List<EmpEntity> selectEmpList();

    EmpEntity getEmpById(String empId);

    EmpEntity createEmp(EmpDto emp, MultipartFile image);

    EmpEntity updateEmp(String empId, EmpDto dto, MultipartFile file);

    // 주민등록번호 중복 여부 + 생년월일만 확인 (등록/저장 없음, 원본은 응답에도 안 남음)
    RrnCheckResultDto checkRrn(String rrn);

}

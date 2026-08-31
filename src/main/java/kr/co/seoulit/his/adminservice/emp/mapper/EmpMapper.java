package kr.co.seoulit.his.adminservice.emp.mapper;

import java.sql.Timestamp;

import kr.co.seoulit.his.adminservice.emp.dto.EmpDto;
import kr.co.seoulit.his.adminservice.emp.entity.EmpEntity;
import org.springframework.stereotype.Component;

@Component
public class EmpMapper {

    // 공통코드 EMP_STATUS_CD — 01: 재직
    private static final String EMP_STATUS_ACTIVE = "01";


    // ========== [등록용] DTO → Entity ==========
    // EMP_NO는 서버에서 자동채번하므로 클라이언트 입력값은 무시한다.
    // EMP_STATUS는 등록 시점에 항상 재직(01)으로 시작한다.
    public EmpEntity toEmpEntity(EmpDto dto) {
        EmpEntity empEntity = new EmpEntity();
        empEntity.setEmpName(dto.getEmpName());
        empEntity.setEmpEmail(dto.getEmpEmail());
        empEntity.setEmpPhone(dto.getEmpPhone());
        empEntity.setHireDate(dto.getHireDate());
        empEntity.setDeptCode(dto.getDeptCode());
        empEntity.setZipCode(dto.getZipCode());
        empEntity.setAddress(dto.getAddress());
        empEntity.setAddressDetail(dto.getAddressDetail());
        empEntity.setEmpStatus(EMP_STATUS_ACTIVE);
        // 등록 시점을 생성일시/수정일시에 함께 넣는다 (ACCOUNT 를 만들 때와 같은 방식)
        Timestamp now = new Timestamp(System.currentTimeMillis());
        empEntity.setCreatedAt(now);
        empEntity.setUpdatedAt(now);
        return empEntity;
    }
}

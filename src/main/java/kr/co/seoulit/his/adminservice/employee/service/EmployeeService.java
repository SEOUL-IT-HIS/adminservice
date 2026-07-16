package kr.co.seoulit.his.adminservice.employee.service;

import kr.co.seoulit.his.adminservice.employee.dto.CreateEmployeeRequest;
import kr.co.seoulit.his.adminservice.employee.dto.EmployeeResponse;
import kr.co.seoulit.his.adminservice.employee.dto.UpdateEmployeeRequest;

import java.util.List;

public interface EmployeeService {

    /**
     * 직원 + 계정 동시 등록.
     * password 평문은 BCrypt 해시 후 ACCOUNT.pw_hash 에만 저장한다.
     */
    EmployeeResponse create(CreateEmployeeRequest request);

    /**
     * 직원 정보 수정.
     * empNo / loginId 는 수정하지 않으며, password 변경은 별도 API 를 사용한다.
     */
    EmployeeResponse update(Long empId, UpdateEmployeeRequest request);

    List<EmployeeResponse> findAll();
}

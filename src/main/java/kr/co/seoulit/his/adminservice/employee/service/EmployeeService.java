package kr.co.seoulit.his.adminservice.employee.service;

import kr.co.seoulit.his.adminservice.employee.dto.CreateEmployeeRequest;
import kr.co.seoulit.his.adminservice.employee.dto.EmployeeResponse;

import java.util.List;

public interface EmployeeService {

    /**
     * 직원 + 계정 동시 등록.
     * password 평문은 BCrypt 해시 후 ACCOUNT.pw_hash 에만 저장한다.
     */
    EmployeeResponse create(CreateEmployeeRequest request);

    List<EmployeeResponse> findAll();
}

package kr.co.seoulit.his.adminservice.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 직원 등록 요청 — hisfrontend CreateEmployeeRequest 와 동일 계약
 * - EMP: 직원 마스터
 * - ACCOUNT: loginId + password(평문) → 서버에서 PW_HASH 로 저장
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateEmployeeRequest {

    @NotBlank(message = "사번은 필수입니다.")
    @Size(max = 20, message = "사번은 20자 이하여야 합니다.")
    private String empNo;

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 100, message = "이름은 100자 이하여야 합니다.")
    private String name;

    @Size(max = 200, message = "이메일은 200자 이하여야 합니다.")
    private String email;

    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다.")
    private String phone;

    /** yyyy-MM-dd */
    private String hireDate;

    /** 공통코드 EMP_STATUS_CD (01=재직, 02=휴직, 03=퇴직). 미전달 시 01 */
    private String empStatus;

    @Size(max = 20, message = "부서코드는 20자 이하여야 합니다.")
    private String deptCode;

    @NotBlank(message = "로그인 ID는 필수입니다.")
    @Size(max = 50, message = "로그인 ID는 50자 이하여야 합니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 4, max = 100, message = "비밀번호는 4자 이상이어야 합니다.")
    private String password;
}

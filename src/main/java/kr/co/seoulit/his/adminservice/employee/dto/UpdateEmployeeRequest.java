package kr.co.seoulit.his.adminservice.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 직원 수정 요청
 * - empNo / loginId 는 식별값이므로 수정 범위에서 제외
 * - password 변경은 별도 API 로 처리
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateEmployeeRequest {

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 100, message = "이름은 100자 이하여야 합니다.")
    private String name;

    @Size(max = 200, message = "이메일은 200자 이하여야 합니다.")
    private String email;

    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다.")
    private String phone;

    /** yyyy-MM-dd */
    private String hireDate;

    /** yyyy-MM-dd */
    private String retireDate;

    /** 공통코드 EMP_STATUS_CD (01=재직, 02=휴직, 03=퇴직) */
    private String empStatus;

    @Size(max = 20, message = "부서코드는 20자 이하여야 합니다.")
    private String deptCode;
}

package kr.co.seoulit.his.adminservice.emp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * [DTO] 주민등록번호 확인 결과
 * - 평문 주민번호는 절대 포함하지 않는다 (중복 여부 + 생년월일만)
 */
@Data
@AllArgsConstructor
public class RrnCheckResultDto {
    private boolean duplicate;
    private LocalDate birthDate;
}

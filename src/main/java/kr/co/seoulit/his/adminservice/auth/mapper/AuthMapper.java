package kr.co.seoulit.his.adminservice.auth.mapper;

import kr.co.seoulit.his.adminservice.auth.dto.AuthDto;
import kr.co.seoulit.his.adminservice.auth.entity.AuthEntity;
import kr.co.seoulit.his.adminservice.emp.entity.EmpEntity;
import org.springframework.stereotype.Component;

/**
 * ACCOUNT + EMPLOYEE → 로그인 응답 AuthDto
 */
@Component
public class AuthMapper {

    public AuthDto toAuthDto(AuthEntity account, EmpEntity emp) {
        AuthDto dto = new AuthDto();
        dto.setAccountId(account.getAccountId());
        dto.setEmpId(account.getEmpId());
        dto.setLoginId(account.getLoginId());
        // 비밀번호 해시는 응답에 넣지 않음
        dto.setPwHash(null);
        dto.setAccountStatus(account.getAccountStatus());
        dto.setFailCount(account.getFailCount());
        dto.setLockedAt(account.getLockedAt());
        dto.setPwChangeAt(account.getPwChangeAt());
        dto.setLastLoginAt(account.getLastLoginAt());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setUpdatedAt(account.getUpdatedAt());

        dto.setEmpName(emp.getEmpName());
        dto.setEmpNo(emp.getEmpNo());
        dto.setDeptCode(emp.getDeptCode());
        return dto;
    }
}

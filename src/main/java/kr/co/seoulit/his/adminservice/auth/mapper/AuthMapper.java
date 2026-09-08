package kr.co.seoulit.his.adminservice.auth.mapper;

import kr.co.seoulit.his.adminservice.auth.dto.AuthDto;
import kr.co.seoulit.his.adminservice.auth.dto.SessionUser;
import kr.co.seoulit.his.adminservice.auth.entity.AuthEntity;
import kr.co.seoulit.his.adminservice.emp.entity.EmpEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ACCOUNT + EMPLOYEE → 로그인 응답 AuthDto / 세션 저장용 SessionUser
 */
@Component
public class AuthMapper {

    /**
     * 세션(Redis)에 넣을 SessionUser 를 만든다.
     *
     * AuthDto 와 달리 비밀번호나 날짜 값은 담지 않는다.
     * 이유는 SessionUser 클래스 주석에 적어두었다.
     */
    public SessionUser toSessionUser(AuthEntity account,
                                     EmpEntity emp,
                                     List<String> roleCodes,
                                     List<String> menuCodes) {
        SessionUser user = new SessionUser();
        user.setAccountId(account.getAccountId());
        user.setAccountStatus(account.getAccountStatus());
        user.setEmpId(account.getEmpId());
        user.setLoginId(account.getLoginId());

        user.setEmpName(emp.getEmpName());
        user.setEmpNo(emp.getEmpNo());
        user.setDeptCode(emp.getDeptCode());

        user.setRoleCodes(roleCodes);
        user.setMenuCodes(menuCodes);
        return user;
    }

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

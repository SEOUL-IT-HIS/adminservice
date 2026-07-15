package kr.co.seoulit.his.adminservice.auth.service;

import kr.co.seoulit.his.adminservice.auth.dto.LoginRequest;
import kr.co.seoulit.his.adminservice.auth.dto.LoginResponse;
import kr.co.seoulit.his.adminservice.auth.entity.Account;
import kr.co.seoulit.his.adminservice.auth.repository.AccountRepository;
import kr.co.seoulit.his.adminservice.common.exception.BusinessException;
import kr.co.seoulit.his.adminservice.common.exception.ErrorCode;
import kr.co.seoulit.his.adminservice.employee.entity.Emp;
import kr.co.seoulit.his.adminservice.employee.repository.EmpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final EmpRepository empRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인 ID / 비밀번호 검증 후 세션용 계정·직원 정보를 반환한다.
     * JWT / Security Filter 연동은 이후 auth 스토리에서 진행한다.
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String loginId = request.getLoginId().trim();

        Account account = accountRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_FAILED));

        if (!passwordEncoder.matches(request.getPassword(), account.getPwHash())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        String status = account.getAccountStatus();
        if ("LOCKED".equalsIgnoreCase(status)) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }
        if ("DISABLED".equalsIgnoreCase(status)) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }
        if (status != null && !"ACTIVE".equalsIgnoreCase(status)) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        Emp emp = empRepository.findById(account.getEmpId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMP_NOT_FOUND));

        return LoginResponse.builder()
                .accountId(account.getAccountId())
                .empId(emp.getEmpId())
                .empNo(emp.getEmpNo())
                .name(emp.getName())
                .loginId(account.getLoginId())
                .accountStatus(account.getAccountStatus())
                .build();
    }
}

package kr.co.seoulit.his.adminservice.auth.service.impl;

import kr.co.seoulit.his.adminservice.auth.dto.AuthDto;
import kr.co.seoulit.his.adminservice.auth.dto.AuthRequestDto;
import kr.co.seoulit.his.adminservice.auth.entity.AuthEntity;
import kr.co.seoulit.his.adminservice.auth.mapper.AuthMapper;
import kr.co.seoulit.his.adminservice.auth.repository.AuthRepository;
import kr.co.seoulit.his.adminservice.auth.service.AuthService;
import kr.co.seoulit.his.adminservice.common.exception.BusinessException;
import kr.co.seoulit.his.adminservice.common.exception.ErrorCode;
import kr.co.seoulit.his.adminservice.emp.entity.EmpEntity;
import kr.co.seoulit.his.adminservice.emp.repository.EmpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * [ServiceImpl] 로그인 검증
 * - ACCOUNT: loginId / pwHash 확인
 * - EMPLOYEE: 재직(01) 확인 후 응답 DTO 구성
 *
 * 참고: 직원등록 시 비밀번호 입력 없음 → 현재는 PW_HASH 평문 비교
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    /** EMP_STATUS_CD — 재직 */
    private static final String EMP_STATUS_ACTIVE = "01";

    private final AuthRepository authRepository;
    private final EmpRepository empRepository;
    private final AuthMapper authMapper;

    @Override
    public AuthDto login(AuthRequestDto request) {
        if (!StringUtils.hasText(request.getLoginId()) || !StringUtils.hasText(request.getPassword())) {
            throw new BusinessException(ErrorCode.AUTH_LOGIN_FIELD_REQUIRED);
        }

        String loginId = request.getLoginId().trim();

        // findByLoginId(...) 는 Optional<AuthEntity> 를 돌려준다 (계정이 없을 수도 있어서).
        // .orElseThrow(...) 는 "값이 있으면 꺼내 쓰고, 없으면 괄호 안의 예외를 던진다"는 뜻이다.
        AuthEntity account = authRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID_CREDENTIALS));

        if (account.getLockedAt() != null) {
            throw new BusinessException(ErrorCode.AUTH_ACCOUNT_LOCKED);
        }

        // 직원등록 과정에 비밀번호 입력 없음 → 당분간 평문 비교
        if (!request.getPassword().equals(account.getPwHash())) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }

        EmpEntity emp = empRepository.findById(account.getEmpId())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID_CREDENTIALS));

        // 아래 두 실패(계정 미존재/비밀번호 불일치)와 재직 상태 아님을 전부 같은 에러코드로 처리한다.
        // 메시지를 다르게 하면 "이 아이디는 존재하는데 휴직중이구나" 처럼 공격자에게
        // 계정 존재 여부를 흘리게 되므로, 일부러 구분하지 않고 뭉뚱그린다.
        if (!EMP_STATUS_ACTIVE.equals(emp.getEmpStatus())) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }

        return authMapper.toAuthDto(account, emp);
    }
}

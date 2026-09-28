package kr.co.seoulit.his.adminservice.auth.service.impl;

import kr.co.seoulit.his.adminservice.auth.dto.AuthRequestDto;
import kr.co.seoulit.his.common.session.SessionUser;
import kr.co.seoulit.his.adminservice.auth.entity.AuthEntity;
import kr.co.seoulit.his.adminservice.auth.mapper.AuthMapper;
import kr.co.seoulit.his.adminservice.auth.repository.AuthRepository;
import kr.co.seoulit.his.adminservice.auth.service.AuthService;
import kr.co.seoulit.his.adminservice.common.exception.BusinessException;
import kr.co.seoulit.his.adminservice.common.exception.ErrorCode;
import kr.co.seoulit.his.adminservice.emp.entity.EmpEntity;
import kr.co.seoulit.his.adminservice.emp.entity.EmpRoleEntity;
import kr.co.seoulit.his.adminservice.emp.repository.EmpRepository;
import kr.co.seoulit.his.adminservice.emp.repository.EmpRoleRepository;
import kr.co.seoulit.his.adminservice.menu.entity.MenuEntity;
import kr.co.seoulit.his.adminservice.menu.repository.MenuRepository;
import kr.co.seoulit.his.adminservice.role.entity.RoleEntity;
import kr.co.seoulit.his.adminservice.role.repository.RoleRepository;
import kr.co.seoulit.his.adminservice.roleMenu.entity.RoleMenuEntity;
import kr.co.seoulit.his.adminservice.roleMenu.repository.RoleMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * [ServiceImpl] 로그인 검증
 * - ACCOUNT: loginId / pwHash 확인
 * - EMPLOYEE: 재직(01) 확인 후 응답 DTO 구성
 * - 비밀번호 5회 연속 실패 시 계정 잠금 (관리자 01 은 제외)
 *
 * 참고: 직원등록 시 비밀번호 입력 없음 → 현재는 PW_HASH 평문 비교
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    /** EMP_STATUS_CD — 재직 */
    private static final String EMP_STATUS_ACTIVE = "01";

    /** ROLE_MENU.CAN_READ — 조회 허용 */
    private static final String CAN_READ_YES = "Y";

    /** 이 횟수만큼 연속으로 비밀번호를 틀리면 계정을 잠근다 */
    private static final int MAX_FAIL_COUNT = 5;

    /** ROLE.ROLE_CODE — 시스템 관리자. 팀원이 같이 쓰는 계정이라 잠금 대상에서 뺀다 */
    private static final String ROLE_CODE_ADMIN = "01";

    private final AuthRepository authRepository;
    private final EmpRepository empRepository;
    private final EmpRoleRepository empRoleRepository;
    private final RoleRepository roleRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final MenuRepository menuRepository;
    private final AuthMapper authMapper;

    /**
     * 클래스 전체는 readOnly 트랜잭션이라 여기서는 쓰기용으로 다시 선언한다.
     * 비밀번호가 틀리면 실패 횟수를 저장한 뒤 BusinessException 을 던지는데,
     * 기본 설정이면 예외가 나는 순간 저장한 내용이 되돌려진다(롤백).
     * noRollbackFor 로 "이 예외에서는 되돌리지 마라"라고 알려줘야 실패 횟수가 DB 에 남는다.
     */
    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    public SessionUser login(AuthRequestDto request) {
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
            recordLoginFail(account);
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

        // 로그인 성공 → 그동안 틀린 횟수는 없던 일로 한다 (연속 실패만 센다)
        if (account.getFailCount() != null && account.getFailCount() > 0) {
            account.setFailCount(0);
            authRepository.save(account);
        }

        return authMapper.toSessionUser(account, emp, findRoleCodes(emp.getEmpId()), findMenuCodes(emp.getEmpId()));
    }

    /**
     * 비밀번호를 틀렸을 때 실패 횟수를 1 올리고, MAX_FAIL_COUNT 에 닿으면 잠근다.
     *
     * 잠금은 LOCKED_AT 에 시각을 넣는 것으로 끝난다.
     * 다음 로그인 때 login() 위쪽의 "LOCKED_AT 이 있으면 막기" 검사가 알아서 막아준다.
     * 관리자(01)는 여러 명이 같이 쓰는 계정이라, 잠기면 아무도 못 들어오므로 세지 않는다.
     */
    private void recordLoginFail(AuthEntity account) {
        if (findRoleCodes(account.getEmpId()).contains(ROLE_CODE_ADMIN)) {
            return;
        }

        int failCount = 0;
        if (account.getFailCount() != null) {
            failCount = account.getFailCount();
        }
        failCount = failCount + 1;
        account.setFailCount(failCount);

        if (failCount >= MAX_FAIL_COUNT) {
            account.setLockedAt(new Timestamp(System.currentTimeMillis()));
        }

        authRepository.save(account);
    }

    /**
     * 이 직원이 가진 역할 코드들을 찾는다. (EMP_ROLE → ROLE)
     *
     * 화면에서는 한 사람에게 역할 하나만 주지만 EMP_ROLE 은 여러 줄이 될 수 있어서 목록으로 다룬다.
     * 역할이 하나도 없으면 빈 목록이 나온다 (로그인은 되고, 권한만 없는 상태).
     */
    private List<String> findRoleCodes(String empId) {
        List<String> roleIds = findRoleIds(empId);

        List<String> roleCodes = new ArrayList<>();
        for (RoleEntity role : roleRepository.findAllById(roleIds)) {
            roleCodes.add(role.getRoleCode());
        }
        return roleCodes;
    }

    /**
     * 이 직원이 볼 수 있는 메뉴 코드들을 찾는다. (EMP_ROLE → ROLE_MENU → MENU)
     *
     * 직원은 메뉴와 직접 연결되지 않는다. 역할을 거쳐서 연결된다.
     * 역할을 두 개 가지면 같은 메뉴가 두 번 나올 수 있으므로 중복은 걸러낸다.
     */
    private List<String> findMenuCodes(String empId) {
        List<String> menuIds = new ArrayList<>();
        for (String roleId : findRoleIds(empId)) {
            for (RoleMenuEntity roleMenu : roleMenuRepository.findByRoleId(roleId)) {
                if (CAN_READ_YES.equals(roleMenu.getCanRead()) && !menuIds.contains(roleMenu.getMenuId())) {
                    menuIds.add(roleMenu.getMenuId());
                }
            }
        }

        List<String> menuCodes = new ArrayList<>();
        for (MenuEntity menu : menuRepository.findAllById(menuIds)) {
            menuCodes.add(menu.getMenuCode());
        }
        return menuCodes;
    }

    /** EMP_ROLE 에서 이 직원에게 배정된 역할 ID 들 */
    private List<String> findRoleIds(String empId) {
        List<String> roleIds = new ArrayList<>();
        for (EmpRoleEntity empRole : empRoleRepository.findByEmpId(empId)) {
            roleIds.add(empRole.getRoleId());
        }
        return roleIds;
    }
}

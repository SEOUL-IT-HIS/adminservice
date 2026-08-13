package kr.co.seoulit.his.adminservice.emp.service.impl;

import java.sql.Timestamp;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import kr.co.seoulit.his.adminservice.auth.entity.AuthEntity;
import kr.co.seoulit.his.adminservice.auth.repository.AuthRepository;
import kr.co.seoulit.his.adminservice.emp.entity.EmpEntity;
import kr.co.seoulit.his.adminservice.emp.entity.EmpRoleEntity;
import kr.co.seoulit.his.adminservice.emp.mybatis.EmpRoleMapper;
import kr.co.seoulit.his.adminservice.emp.repository.EmpRepository;
import kr.co.seoulit.his.adminservice.emp.repository.EmpRoleRepository;
import kr.co.seoulit.his.adminservice.emp.service.EmpService;
import kr.co.seoulit.his.adminservice.emp.dto.EmpDto;
import kr.co.seoulit.his.adminservice.emp.mapper.EmpMapper;
import kr.co.seoulit.his.adminservice.common.exception.BusinessException;
import kr.co.seoulit.his.adminservice.common.exception.ErrorCode;
import kr.co.seoulit.his.adminservice.role.repository.RoleRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EmpServiceImpl implements EmpService {

    // 사번 접두사 뒤 순번 자리수 (EYYYYMM001 형태)
    private static final int EMP_NO_SEQ_DIGITS = 3;
    private static final DateTimeFormatter EMP_NO_MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyyMM");
    // 사번 채번 충돌(동시등록) 시 재시도 횟수
    private static final int EMP_NO_MAX_RETRY = 5;
    // 신규 계정 초기 비밀번호 (임시 고정값, 추후 정책 확정 시 변경)
    private static final String DEFAULT_PW_HASH = "1111";
    // ACCOUNT_STATUS_CD(공통코드) — 01: 활성
    private static final String ACCOUNT_STATUS_ACTIVE = "01";

    private final EmpMapper empMapper;
    private final EmpRepository empRepository;
    private final AuthRepository authRepository;
    private final EmpRoleRepository empRoleRepository;
    private final RoleRepository roleRepository;
    private final EmpRoleMapper empRoleMapper;

    // ========== [목록] ==========
    @Override
    public List<EmpEntity> selectEmpList() {
        return empRepository.findAll();
    }

    // ========== [등록] ==========
    // 직원 등록과 동시에 로그인 계정(ACCOUNT)을 생성한다.
    // - LOGIN_ID: 자동채번된 EMP_NO 그대로 사용
    // - PW_HASH: 임시 고정값(DEFAULT_PW_HASH)
    @Override
    public EmpEntity createEmp(EmpDto dto) {
        EmpEntity savedEmp = saveEmpWithGeneratedEmpNo(dto);
        createAccountFor(savedEmp);
        return savedEmp;
    }

    // 사번(EMP_NO)은 월별로 리셋되는 UNIQUE 값이라 동시 등록 시 충돌할 수 있어
    // 충돌하면 다음 순번으로 재시도한다.
    private EmpEntity saveEmpWithGeneratedEmpNo(EmpDto dto) {
        DataIntegrityViolationException lastError = null;
        for (int attempt = 1; attempt <= EMP_NO_MAX_RETRY; attempt++) {
            EmpEntity empEntity = empMapper.toEmpEntity(dto);
            empEntity.setEmpNo(generateNextEmpNo());
            try {
                return empRepository.saveAndFlush(empEntity);
            } catch (DataIntegrityViolationException e) {
                lastError = e;
            }
        }
        // 루프가 여기까지 왔다는 건 EMP_NO_MAX_RETRY번 전부 실패했다는 뜻이라 lastError는 항상 값이 있다
        // (성공했으면 위 return으로 이미 메서드를 빠져나갔을 것이기 때문)
        throw lastError;
    }

    // 이번 달 접두사(prefix)로 시작하는 가장 최근 사번을 찾아 다음 순번을 계산한다.
    // findTopBy...는 Optional<EmpEntity>를 돌려주는데, 값이 있으면(.map) 그 사번의 끝자리 숫자+1을,
    // 없으면(.orElse) 이번 달 첫 등록이라는 뜻이므로 1을 순번으로 쓴다.
    private String generateNextEmpNo() {
        String prefix = "E" + YearMonth.now().format(EMP_NO_MONTH_FORMAT);
        int nextSeq = empRepository.findTopByEmpNoStartingWithOrderByEmpNoDesc(prefix)
                .map(e -> Integer.parseInt(e.getEmpNo().substring(prefix.length())) + 1)
                .orElse(1);
        return prefix + String.format("%0" + EMP_NO_SEQ_DIGITS + "d", nextSeq);
    }

    private void createAccountFor(EmpEntity emp) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        AuthEntity account = new AuthEntity();
        account.setEmpId(emp.getEmpId());
        account.setLoginId(emp.getEmpNo());
        account.setPwHash(DEFAULT_PW_HASH);
        account.setAccountStatus(ACCOUNT_STATUS_ACTIVE);
        account.setFailCount(0);
        account.setPwChangeAt(now);
        account.setCreatedAt(now);
        account.setUpdatedAt(now);
        authRepository.save(account);
    }

    // ========== [수정] ==========
    @Override
    public EmpEntity updateEmp(String empId, EmpDto dto) {
        EmpEntity empEntity = empRepository.findById(empId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMP_NOT_FOUND));
        empEntity.setEmpName(dto.getEmpName());
        empEntity.setEmpEmail(dto.getEmpEmail());
        empEntity.setEmpPhone(dto.getEmpPhone());
        empEntity.setRetireDate(dto.getRetireDate());
        empEntity.setEmpStatus(dto.getEmpStatus());
        empEntity.setDeptCode(dto.getDeptCode());

        List<String> roleIds = dto.getRoleIds();
        if (roleIds != null) {
            // 1) 먼저 검증 — 잘못된 역할이 하나라도 있으면 여기서 멈추고, 기존 데이터는 안 건드림
            for (String roleId : roleIds) {
                roleRepository.findById(roleId)
                        .filter(role -> "Y".equals(role.getUseYn()))
                        .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
            }

            // 2) 검증 통과했으면 기존 배정과 비교해서 "추가할 것"/"뺄 것"만 계산한다.
            // (예전엔 무조건 다 지우고 다 다시 넣었는데, 그러면 안 바뀐 역할까지
            //  assignedAt/assignedBy가 이번 수정 시각으로 덮어써지는 문제가 있었다.
            //  MyBatis로 diff만 delete/insert 하면 그 문제도, 이전의 flush 순서 문제도 같이 해결된다 —
            //  삭제 대상과 삽입 대상이 겹치지 않아서 순서가 뒤집혀도 충돌할 행이 없다.)
            List<String> existingRoleIds = empRoleRepository.findByEmpId(empId).stream()
                    .map(EmpRoleEntity::getRoleId)
                    .toList();

            Set<String> toAdd = new HashSet<>(roleIds);
            toAdd.removeAll(existingRoleIds);

            Set<String> toRemove = new HashSet<>(existingRoleIds);
            toRemove.removeAll(roleIds);

            // 3) 뺄 것만 삭제
            if (!toRemove.isEmpty()) {
                empRoleMapper.deleteByEmpIdAndRoleIds(empId, toRemove);
            }

            // 4) 추가할 것만 삽입 (그대로 유지되는 역할은 건드리지 않으므로 원래 assignedAt/assignedBy 보존됨)
            Timestamp now = new Timestamp(System.currentTimeMillis());
            for (String roleId : toAdd) {
                EmpRoleEntity empRole = new EmpRoleEntity();
                empRole.setEmpRoleId(UUID.randomUUID().toString());
                empRole.setEmpId(empId);
                empRole.setRoleId(roleId);
                empRole.setAssignedBy(dto.getAssignedBy());
                empRole.setAssignedAt(now);
                empRoleMapper.insert(empRole);
            }

            empEntity.setRoleIds(roleIds);
        }
        return empRepository.save(empEntity);
    }

    // ========== [상세] ==========
    @Override
    public EmpEntity getEmpById(String empId) {
        EmpEntity empEntity = empRepository.findById(empId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMP_NOT_FOUND));

        List<String> roleIds = empRoleRepository.findByEmpId(empId).stream()
                .map(EmpRoleEntity::getRoleId)
                .toList();
        empEntity.setRoleIds(roleIds);

        return empEntity;
    }
}

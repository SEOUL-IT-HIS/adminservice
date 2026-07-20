package kr.co.seoulit.his.adminservice.employee.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.seoulit.his.adminservice.auth.entity.Account;
import kr.co.seoulit.his.adminservice.auth.repository.AccountRepository;
import kr.co.seoulit.his.adminservice.common.exception.BusinessException;
import kr.co.seoulit.his.adminservice.common.exception.ErrorCode;
import kr.co.seoulit.his.adminservice.employee.dto.CreateEmployeeRequest;
import kr.co.seoulit.his.adminservice.employee.dto.EmployeeResponse;
import kr.co.seoulit.his.adminservice.employee.dto.UpdateEmployeeRequest;
import kr.co.seoulit.his.adminservice.employee.entity.Emp;
import kr.co.seoulit.his.adminservice.employee.repository.EmpRepository;
import kr.co.seoulit.his.adminservice.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final EmpRepository empRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest request) {
        if (empRepository.existsByEmpNo(request.getEmpNo())) {
            throw new BusinessException(ErrorCode.EMP_NO_DUPLICATE);
        }
        if (accountRepository.existsByLoginId(request.getLoginId())) {
            throw new BusinessException(ErrorCode.LOGIN_ID_DUPLICATE);
        }

        Emp emp = new Emp();
        emp.setEmpId(nextSeq("EMP_SEQ"));
        emp.setEmpNo(request.getEmpNo().trim());
        emp.setName(request.getName().trim());
        emp.setEmail(blankToNull(request.getEmail()));
        emp.setPhone(blankToNull(request.getPhone()));
        emp.setHireDate(parseDate(request.getHireDate()));
        emp.setRetireDate(null);
        // 공통코드 EMP_STATUS_CD: 01=재직 (미전달 시 기본값)
        emp.setEmpStatus(StringUtils.hasText(request.getEmpStatus()) ? request.getEmpStatus() : "01");
        emp.setDeptCode(blankToNull(request.getDeptCode()));
        Emp savedEmp = empRepository.saveAndFlush(emp);

        Account account = new Account();
        account.setAccountId(nextSeq("ACCOUNT_SEQ"));
        account.setEmpId(savedEmp.getEmpId());
        account.setLoginId(request.getLoginId().trim());
        account.setPwHash(passwordEncoder.encode(request.getPassword()));
        account.setAccountStatus("ACTIVE");
        Account savedAccount = accountRepository.saveAndFlush(account);

        return toResponse(savedEmp, savedAccount);
    }

    @Override
    @Transactional
    public EmployeeResponse update(Long empId, UpdateEmployeeRequest request) {
        Emp emp = empRepository.findById(empId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMP_NOT_FOUND));

        emp.setName(request.getName().trim());
        emp.setEmail(blankToNull(request.getEmail()));
        emp.setPhone(blankToNull(request.getPhone()));
        emp.setHireDate(parseDate(request.getHireDate()));
        emp.setRetireDate(parseDate(request.getRetireDate()));
        if (StringUtils.hasText(request.getEmpStatus())) {
            emp.setEmpStatus(request.getEmpStatus().trim());
        }
        emp.setDeptCode(blankToNull(request.getDeptCode()));

        Emp savedEmp = empRepository.saveAndFlush(emp);
        Account account = accountRepository.findByEmpId(savedEmp.getEmpId()).orElse(null);
        return toResponse(savedEmp, account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAll() {
        List<Emp> emps = empRepository.findAll();
        List<Long> empIds = emps.stream().map(Emp::getEmpId).toList();
        Map<Long, Account> accountByEmpId = accountRepository.findByEmpIdIn(empIds).stream()
                .collect(Collectors.toMap(Account::getEmpId, Function.identity()));

        return emps.stream()
                .map(emp -> toResponse(emp, accountByEmpId.get(emp.getEmpId())))
                .toList();
    }

    private Long nextSeq(String sequenceName) {
        Object value = entityManager
                .createNativeQuery("SELECT " + sequenceName + ".NEXTVAL FROM DUAL")
                .getSingleResult();
        return ((Number) value).longValue();
    }

    private EmployeeResponse toResponse(Emp emp, Account account) {
        return EmployeeResponse.builder()
                .empId(emp.getEmpId())
                .empNo(emp.getEmpNo())
                .name(emp.getName())
                .email(emp.getEmail())
                .phone(emp.getPhone())
                .hireDate(formatDate(emp.getHireDate()))
                .retireDate(formatDate(emp.getRetireDate()))
                .empStatus(emp.getEmpStatus())
                .deptCode(emp.getDeptCode())
                .createdAt(formatDateTime(emp.getCreatedAt()))
                .updatedAt(formatDateTime(emp.getUpdatedAt()))
                .loginId(account != null ? account.getLoginId() : null)
                .accountStatus(account != null ? account.getAccountStatus() : null)
                .build();
    }

    private static String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private static LocalDate parseDate(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return LocalDate.parse(value.trim(), DATE);
    }

    private static String formatDate(LocalDate value) {
        return value == null ? null : value.format(DATE);
    }

    private static String formatDateTime(LocalDateTime value) {
        return value == null ? null : value.format(DATE_TIME);
    }
}

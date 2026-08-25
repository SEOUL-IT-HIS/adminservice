package kr.co.seoulit.his.adminservice.emp.service.impl;

import java.sql.Timestamp;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import kr.co.seoulit.his.adminservice.auth.entity.AuthEntity;
import kr.co.seoulit.his.adminservice.auth.repository.AuthRepository;
import kr.co.seoulit.his.adminservice.emp.entity.EmpEntity;
import kr.co.seoulit.his.adminservice.emp.repository.EmpRepository;
import kr.co.seoulit.his.adminservice.emp.service.EmpService;
import kr.co.seoulit.his.adminservice.emp.dto.EmpDto;
import kr.co.seoulit.his.adminservice.emp.mapper.EmpMapper;
import kr.co.seoulit.his.adminservice.common.exception.BusinessException;
import kr.co.seoulit.his.adminservice.common.exception.ErrorCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.seoulit.his.adminservice.storage.seaweed.dto.UploadResultDto;
import kr.co.seoulit.his.adminservice.storage.seaweed.service.SeaweedStorageService;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;


import lombok.RequiredArgsConstructor;

@Slf4j
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
    private final SeaweedStorageService seaweedStorageService;

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
    public EmpEntity createEmp(EmpDto dto, MultipartFile image) {
        EmpEntity savedEmp = saveEmpWithGeneratedEmpNo(dto);
        createAccountFor(savedEmp);
        attachImage(savedEmp, image);
        return empRepository.save(savedEmp);
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
    public EmpEntity updateEmp(String empId, EmpDto dto, MultipartFile image) {
        EmpEntity empEntity = empRepository.findById(empId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMP_NOT_FOUND));
        empEntity.setEmpName(dto.getEmpName());
        empEntity.setEmpEmail(dto.getEmpEmail());
        empEntity.setEmpPhone(dto.getEmpPhone());
        empEntity.setRetireDate(dto.getRetireDate());
        empEntity.setEmpStatus(dto.getEmpStatus());
        empEntity.setDeptCode(dto.getDeptCode());
        attachImage(empEntity, image);
        return empRepository.save(empEntity);
    }

    // 이미지가 있으면 SeaweedFS에 업로드해서 URL/파일명을 엔티티에 채운다.
// 실패해도 예외를 밖으로 던지지 않는다 — 직원 저장 자체는 그대로 성공 처리하기로 했으므로(A안).
    private void attachImage(EmpEntity empEntity, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return;
        }
        try {
            if (empEntity.getProfileImageUrl() != null) {
                seaweedStorageService.delete(empEntity.getProfileImageUrl());
            }
            UploadResultDto result = seaweedStorageService.upload(image);
            empEntity.setProfileImageUrl(result.getUrl());
            empEntity.setProfileImageFid(result.getFileName());
        } catch (Exception e) {
            log.warn("직원({}) 이미지 업로드 실패, 직원 정보 저장은 계속 진행", empEntity.getEmpId(), e);
        }
    }

    // ========== [상세] ==========
    @Override
    public EmpEntity getEmpById(String empId) {
        return empRepository.findById(empId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMP_NOT_FOUND));
    }
}

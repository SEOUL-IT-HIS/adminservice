package kr.co.seoulit.his.adminservice.emp.service.impl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import kr.co.seoulit.his.adminservice.emp.dto.RrnCheckResultDto;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

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
    // 프로필 사진으로 허용할 이미지 타입
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final EmpMapper empMapper;
    private final EmpRepository empRepository;
    private final AuthRepository authRepository;
    private final SeaweedStorageService seaweedStorageService;

    // 주민등록번호 해시용 비밀키 (application.properties). 이 키가 없으면 해시를 역추적하기 훨씬 어려워진다.
    @Value("${rrn.hash-secret}")
    private String rrnHashSecret;

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
        // 평문 주민번호는 여기서만 잠깐 쓰고, 저장되는 건 해시값 + 생년월일뿐이다.
        applyRrn(savedEmp, dto.getRrn());
        createAccountFor(savedEmp);
        attachImage(savedEmp, image);
        return empRepository.save(savedEmp);
    }

    // 주민번호가 없으면 아무 것도 안 함. 있으면: 중복이면 여기서 막고,
    // 아니면 해시(rrnHash)와 생년월일(birthDate)을 채운다.
    // (계정/직원 row가 만들어지기 전에 먼저 검사해서, 중복일 때 빈 계정만 남는 걸 방지)
    private void applyRrn(EmpEntity empEntity, String rrn) {
        if (!StringUtils.hasText(rrn)) {
            return;
        }
        String digitsOnly = rrn.replace("-", "").trim();
        String rrnHash = hashRrn(digitsOnly);
        if (empRepository.existsByRrnHash(rrnHash)) {
            throw new BusinessException(ErrorCode.EMP_RRN_DUPLICATE);
        }
        empEntity.setRrnHash(rrnHash);
        empEntity.setBirthDate(parseBirthDateFromRrn(digitsOnly));
    }

    // HMAC-SHA256: 비밀키(rrnHashSecret) 없이는 이 해시값만 보고 원래 주민번호를 역추적할 수 없다.
    private String hashRrn(String rrn) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(rrnHashSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hashBytes = mac.doFinal(rrn.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("주민번호 해시 처리 중 오류가 발생했습니다.", e);
        }
    }

    // 주민번호 앞 6자리(생년월일) + 7번째 자리(성별/세기 구분)로 생년월일을 계산한다.
    // 1,2,5,6 → 1900년대 / 3,4,7,8 → 2000년대. 그 외(9,0 등 옛날 방식)는 지원하지 않고 null.
    // LocalDate는 시각/시간대가 없는 순수 날짜라 타임존 변환으로 하루 밀리는 일이 없다.
    private LocalDate parseBirthDateFromRrn(String rrn) {
        if (rrn.length() < 7) {
            return null;
        }
        char genderDigit = rrn.charAt(6);
        int century;
        if (genderDigit == '1' || genderDigit == '2' || genderDigit == '5' || genderDigit == '6') {
            century = 1900;
        } else if (genderDigit == '3' || genderDigit == '4' || genderDigit == '7' || genderDigit == '8') {
            century = 2000;
        } else {
            return null;
        }
        try {
            int year = century + Integer.parseInt(rrn.substring(0, 2));
            int month = Integer.parseInt(rrn.substring(2, 4));
            int day = Integer.parseInt(rrn.substring(4, 6));
            return LocalDate.of(year, month, day);
        } catch (Exception e) {
            // 형식이 이상해서 날짜로 못 만들면(예: 13월) 그냥 비워둔다 — 등록 자체를 막을 정도는 아니라서
            return null;
        }
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
        empEntity.setZipCode(dto.getZipCode());
        empEntity.setAddress(dto.getAddress());
        empEntity.setAddressDetail(dto.getAddressDetail());
        empEntity.setMedRoleCode(dto.getMedRoleCode());
        // 수정할 때마다 수정일시를 갱신한다
        empEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        attachImage(empEntity, image);
        return empRepository.save(empEntity);
    }

    // 이미지가 있으면 SeaweedFS에 업로드해서 URL/파일명을 엔티티에 채운다.
// 실패해도 예외를 밖으로 던지지 않는다 — 직원 저장 자체는 그대로 성공 처리하기로 했으므로(A안).
    private void attachImage(EmpEntity empEntity, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return;
        }

        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new BusinessException(ErrorCode.INVALID_IMAGE_TYPE);
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

    // ========== [주민등록번호 확인] ==========
    // 등록/저장은 하지 않고, 중복 여부와 생년월일만 돌려준다. 원본 주민번호는 응답에도 안 담는다.
    @Override
    public RrnCheckResultDto checkRrn(String rrn) {
        if (!StringUtils.hasText(rrn)) {
            return new RrnCheckResultDto(false, null);
        }
        String digitsOnly = rrn.replace("-", "").trim();
        boolean duplicate = empRepository.existsByRrnHash(hashRrn(digitsOnly));
        LocalDate birthDate = parseBirthDateFromRrn(digitsOnly);
        return new RrnCheckResultDto(duplicate, birthDate);
    }

    // ========== [상세] ==========
    @Override
    public EmpEntity getEmpById(String empId) {
        return empRepository.findById(empId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMP_NOT_FOUND));
    }
}

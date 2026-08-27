package kr.co.seoulit.his.adminservice.emp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "EMPLOYEE")
public class EmpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "EMP_ID", length = 36)
    private String empId;

    @Column(name = "EMP_NO")
    private String empNo;

    @Column(name = "NAME")
    private String empName;

    @Column(name = "EMAIL")
    private String empEmail;

    @Column(name = "PHONE")
    private String empPhone;

    @Column(name = "HIRE_DATE")
    private Date hireDate;

    @Column(name = "RETIRE_DATE")
    private Date retireDate;

    @Column(name = "EMP_STATUS")
    private String empStatus;

    @Column(name = "DEPT_CODE")
    private String deptCode;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    /** SeaweedFS에 저장된 프로필 이미지 조회용 URL */
    @Column(name = "PROFILE_IMAGE_URL")
    private String profileImageUrl;

    /** SeaweedFS에 저장된 프로필 이미지 파일 ID */
    @Column(name = "PROFILE_IMAGE_FID")
    private String profileImageFid;

    @Column(name = "ZIP_CODE")
    private String zipCode;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "ADDRESS_DETAIL")
    private String addressDetail;

    /** 공통코드 MED_ROLE_CD 그룹 값 (의사/간호사/전문의/전공의/기타) */
    @Column(name = "MED_ROLE_CODE")
    private String medRoleCode;

    /** 주민등록번호는 저장하지 않고, HMAC-SHA256 해시값만 저장 (중복 체크 전용) */
    @Column(name = "RRN_HASH", length = 64)
    private String rrnHash;

    /**
     * 등록 시 주민등록번호 앞자리에서만 추출해서 저장 (원본 주민번호는 남기지 않음)
     * Date(시각+시간대 있음) 대신 LocalDate(그냥 날짜)를 써서 시간대 변환으로
     * 하루 밀리는 문제를 아예 없앤다.
     */
    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;
}

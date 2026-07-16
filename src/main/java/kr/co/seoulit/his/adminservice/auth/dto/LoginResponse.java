package kr.co.seoulit.his.adminservice.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/**
 * 로그인 성공 응답 DTO.
 * HttpSession 에 저장하므로 Serializable 을 구현한다.
 */
@Getter
@Builder
public class LoginResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long accountId;
    private Long empId;
    private String empNo;
    private String name;
    private String loginId;
    private String accountStatus;
}

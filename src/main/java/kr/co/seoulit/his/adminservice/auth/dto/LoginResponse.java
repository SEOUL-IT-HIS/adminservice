package kr.co.seoulit.his.adminservice.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private Long accountId;
    private Long empId;
    private String empNo;
    private String name;
    private String loginId;
    private String accountStatus;
}

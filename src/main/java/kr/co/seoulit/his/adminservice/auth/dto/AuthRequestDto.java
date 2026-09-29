package kr.co.seoulit.his.adminservice.auth.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDto {

    // ----- 로그인 (POST /api/admin/auth/login) -----
    private String loginId;
    private String password;

    // ----- 비밀번호 변경 (PUT /api/admin/auth/password) -----
    // 누구의 비밀번호인지는 받지 않는다. 대상은 항상 세션의 로그인 사용자 본인이다.
    private String currentPassword;
    private String newPassword;
}

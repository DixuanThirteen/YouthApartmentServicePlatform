package com.yasp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private Long id;
    private String username;
    private Role role;
    private Integer code;
    private String message;
    private String token;
    private String refreshToken;
    private String provider;    //user和admin这一栏为空

    public enum Role{
        ADMIN, USER, PROVIDER_Admin, PROVIDER_Staff
    }

}

package com.yasp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String username;
    private Role role;
    private Integer code;
    private String message;
    private String token;
    private String refreshToken;

    public enum Role{
        ADMIN, USER, PROVIDER_Admin, PROVIDER_Staff
    }

}

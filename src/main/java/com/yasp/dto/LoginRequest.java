package com.yasp.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "password")
public class LoginRequest {

    private String username;

    private String password;

    private Boolean rememberMe;
}

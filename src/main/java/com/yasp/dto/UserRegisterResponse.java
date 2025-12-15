package com.yasp.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRegisterResponse {
    private String username;
    private String nickName;
    private Integer code;
    private String message;
}

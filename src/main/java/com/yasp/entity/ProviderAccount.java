package com.yasp.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProviderAccount {

    private Long id;

    private Long providerId;

    private String username;

    private String email;

    private String phone;

    private String password;

    private Integer role;

    private Integer status;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

}

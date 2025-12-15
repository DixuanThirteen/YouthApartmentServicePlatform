package com.yasp.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Admin {

    private Long id;

    private String username;

    private String realName;

    private String password;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

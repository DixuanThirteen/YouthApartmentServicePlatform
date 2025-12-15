package com.yasp.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体，对应表 users
 */
@Data
public class User {

    private Long id;

    private String username;

    private String nickName;

    private String realName;
    private String password;

    private String hashPassword;

    private String idNumber;

    private String phone;

    private String email; // 原表允许为空

    private LocalDate bornDate;

    private Gender gender; // ENUM('male','female')

    private String avatar = "/images/DefaultAvatar.png";

    private String introduction;

    private String region;

    private Degree degree; // ENUM('below','college','bachelor','master','doctorate','above')

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;




    public enum Gender {
        male, female
    }

    public enum Degree {
        below,
        college,
        bachelor,
        master,
        doctorate,
        above
    }
}
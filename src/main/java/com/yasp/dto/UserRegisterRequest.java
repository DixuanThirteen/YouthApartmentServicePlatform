package com.yasp.dto;

import com.yasp.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString(exclude = "password")
public class UserRegisterRequest {
    private String username;
    private String nickName;
    private String realName;
    private String password;
    private String confirmPassword;
    private String idNumber;
    private String phone;
    private String email;
    private LocalDate bornDate;
    private User.Gender gender;
    private String avatar;
    private String introduction;
    private String region;
    private User.Degree degree;
}

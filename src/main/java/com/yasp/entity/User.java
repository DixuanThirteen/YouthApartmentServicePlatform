package com.yasp.entity;

import lombok.Data;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "users") // 表名建议为复数“users”
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Column(nullable=false, unique=true)
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @Column(nullable=false)
    @NotBlank(message = "真实姓名不能为空")
    private String realname;

    @Column(nullable=false)
    @NotBlank(message = "密码不能为空")
    private String password;

    @Column(nullable=false, unique=true, length=18)
    @Pattern(regexp="^\\d{17}[\\dXx]$", message="身份证号码格式不正确")
    private String idnumber;

    @Column(nullable=false, length=11)
    @Pattern(regexp="^\\d{11}$", message="手机号必须为11位数字")
    private String phone;

    @Column(nullable=false, unique=true, length=100)
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Min(value = 0, message = "年龄不能小于0")
    @Max(value = 150, message = "年龄不能大于150")
    private Integer age;

    private LocalDate bornDate;

    @Pattern(regexp= "[男女]", message="性别只能是男、女")
    private String sex; // "男"、"女"、"其他"

    @Column(nullable=false)
    private String avatar; // 头像URL

    private String introduction;
}
package com.yasp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体，对应表 users
 */
@Entity
@Data
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_users_idnumber", columnNames = "idnumber"),
                @UniqueConstraint(name = "uk_users_phone", columnNames = "phone"),
                @UniqueConstraint(name = "uk_users_email", columnNames = "email")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 30, unique = true)
    @NotBlank(message = "用户名不能为空")
    @Size(max = 30, message = "用户名长度不能超过30")
    private String username;

    @Column(nullable = false, length = 30) // 原表不唯一
    @NotBlank(message = "昵称不能为空")
    @Size(max = 30, message = "昵称长度不能超过30")
    private String nickname;

    @Column(name = "realname", nullable = false, length = 100)
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 100, message = "真实姓名长度不能超过100")
    private String realname;

    @Column(nullable = false, length = 30) // 原表是 VARCHAR(30)；若用哈希建议改成 60
    @NotBlank(message = "密码不能为空")
    @Size(max = 30, message = "密码长度不能超过30")
    private String password;

    @Column(nullable = false, length = 18, unique = true)
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^\\d{17}[0-9Xx]$", message = "身份证号码格式不正确")
    private String idnumber;

    @Column(nullable = false, length = 11, unique = true)
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^[0-9]{11}$", message = "手机号必须为11位数字")
    private String phone;

    @Column(length = 100, unique = true)
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100")
    private String email; // 原表允许为空

    @Column(name = "born_date", nullable = false)
    @NotNull(message = "出生日期不能为空")
    private LocalDate bornDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 6)
    @NotNull(message = "性别不能为空")
    private Gender gender; // ENUM('male','female')

    @Column(length = 255, nullable = false)
    @NotBlank(message = "头像不能为空")
    private String avatar = "/images/DefaultAvatar.png";

    @Lob
    @Column
    private String introduction;

    @Column(length = 100)
    @Size(max = 100, message = "地区长度不能超过100")
    private String region;

    @Enumerated(EnumType.STRING)
    @Column(name = "degree", length = 10)
    private Degree degree; // ENUM('below','college','bachelor','master','doctorate','above')

    @Column(name = "register_date", nullable = false, updatable = false)
    @NotNull(message = "注册日期不能为空")
    private LocalDate registerDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (avatar == null || avatar.isBlank()) {
            avatar = "/images/DefaultAvatar.png";
        }
        if (registerDate == null) {
            registerDate = LocalDate.now();
        }
    }

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
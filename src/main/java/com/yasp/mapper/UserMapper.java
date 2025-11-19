package com.yasp.mapper;

import com.yasp.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User selectByUsername(@Param("username") String username);

    int insertUser(User user);

    int updateUser(User user);// 不改密码 只更新个人信息

    int deleteByUsername(String username);

    int updatePassword(User user);     // 只改密码

    User selectByPhone(@Pattern(regexp="^\\d{11}$", message="手机号必须为11位数字") String phone);

    User selectByEmail(@NotBlank(message = "邮箱不能为空") @Email(message = "邮箱格式不正确") String email);

    int updateAvatar(@Param("username") String username, @Param("avatar") String avatar);

    int updateIntroduction(@Param("username") String username, @Param("introduction") String introduction);

    int updateNickname(@Param("username") String username, @Param("nickname") String nickname);
}

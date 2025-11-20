package com.yasp.mapper;

import com.yasp.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    //查找方法
    //通过用户名查用户
    User selectByUsername(@Param("username") String username);

    //通过手机号查用户
    User selectByPhone(@Pattern(regexp="^\\d{11}$", message="手机号必须为11位数字") String phone);

    //通过邮箱查用户
    User selectByEmail(@NotBlank(message = "邮箱不能为空") @Email(message = "邮箱格式不正确") String email);

    //增加用户
    int insertUser(User user);

    //删除用户
    int deleteByUsername(@Param("username") String username);

    //更新方法
    // 只更新头像、昵称、个人介绍、地区。
    int updateUser(User user);

    // 改密码
    int updatePassword(User user);

    //改手机号
    int updatePhone(@Param("username") String username, @Param("phone") String phone);

    //改邮箱
    int updateEmail(@Param("username") String username, @Param("email") String email);

    //改学位信息
    int updateDegree(@Param("username") String username, @Param("degree") String degree);


}

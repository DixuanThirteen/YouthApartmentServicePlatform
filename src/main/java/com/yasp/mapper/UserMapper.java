package com.yasp.mapper;

import com.yasp.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    // 查找方法
    // 通过用户名查用户
    User selectByUsername(@Param("username") String username);

    // 通过手机号查用户
    User selectByPhone(@Param("phone") String phone);

    // 通过邮箱查用户
    User selectByEmail(@Param("email") String email);

    // 通过id查用户
    User selectById(@Param("id") Long id);

    // 注册用户
    int insertUser(User user);

    //更新用户信息
    int updateUser(User user);

    //更新密码
    int updatePassword(@Param("id") Long id, @Param("password") String password);

}

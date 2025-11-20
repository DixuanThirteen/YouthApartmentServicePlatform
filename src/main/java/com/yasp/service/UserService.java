package com.yasp.service;

import com.yasp.entity.User;
import com.yasp.exception.BusinessException;
import com.yasp.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service // 1. 标识本类是Service组件，交给Spring容器管理，属于业务逻辑层
public class UserService {
    @Autowired // 2. 由Spring自动装配UserMapper实例（依赖注入，简化手动new对象）
    private UserMapper userMapper;

    //注册
    public boolean register(User user) {
        // 业务：可加是否存在校验，这里简化直接插入
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        if (userMapper.selectByPhone(user.getPhone()) != null) {
            throw new BusinessException("手机号已被使用");
        }
        if (userMapper.selectByEmail(user.getEmail()) != null) {
            throw new BusinessException("邮箱已被使用");
        }

        return userMapper.insertUser(user) > 0;
    }

    //查找方法
    //通过用户名查用户
    public User findByUsername(String username) {
        // 调用MyBatis Mapper查询用户
        return userMapper.selectByUsername(username);
    }

    //通过手机号查用户
    public User findByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }

    //通过邮箱查用户
    public User findByEmail(String email) {
        return userMapper.selectByEmail(email);
    }


    //更新方法
    // 只更新头像、昵称、个人介绍、地区。
    public boolean updateUser(User user) {
        User user1 = userMapper.selectByUsername(user.getUsername());

        if (Objects.isNull(user1)) {
            throw new BusinessException("用户不存在");
        }

        return userMapper.updateUser(user) > 0;
    }

    // 改密码
    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        User temp = new User();
        temp.setUsername(username);
        temp.setPassword(newPassword);

        User oldUser = userMapper.selectByUsername(username);

        if (Objects.isNull(oldUser)) {
            throw new BusinessException("用户不存在");
        }

        if (!Objects.equals(oldPassword,oldUser.getPassword())){
            throw new BusinessException("旧密码错误");
        }

        if (Objects.equals(oldUser.getPassword(), temp.getPassword())) {
            throw new BusinessException("新密码不能与旧密码相同");
        }

        return userMapper.updatePassword(temp) > 0;
    }

    //更改手机号
    public boolean updatePhone(String username, String oldPhone, String phone) {
        if (Objects.equals(oldPhone, phone)) {
            throw new BusinessException("新手机号不能与旧手机号相同");
        }

        if (userMapper.selectByPhone(phone) != null) {
            throw new BusinessException("手机号已被使用");
        }
        return userMapper.updatePhone(username, phone) > 0;
    }

    //更改邮箱
    public boolean updateEmail(String username, String oldEmail, String email) {
        if (Objects.equals(oldEmail, email)) {
            throw new BusinessException("新邮箱不能与旧邮箱相同");
        }

        if (userMapper.selectByEmail(email) != null) {
            throw new BusinessException("邮箱已被使用");
        }
        return userMapper.updateEmail(username, email) > 0;
    }

    //更改学位信息
    public boolean updateDegree(String username, String degree) {
        return userMapper.updateDegree(username, degree) > 0;
    }

    //删除用户
    public boolean deleteByUsername(String username) {
        return userMapper.deleteByUsername(username) > 0;
    }
}
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

    public User findByUsername(String username) {
        // 调用MyBatis Mapper查询用户
        return userMapper.selectByUsername(username);
    }

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

    public boolean updateUser(User user) {
        // 更新基本信息（不含密码）
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        if (userMapper.selectByPhone(user.getPhone()) != null) {
            throw new BusinessException("手机号已被使用");
        }
        if (userMapper.selectByEmail(user.getEmail()) != null) {
            throw new BusinessException("邮箱已被使用");
        }

        return userMapper.updateUser(user) > 0;
    }

    public boolean updatePassword(String username, String newPassword) {
        User temp = new User();
        temp.setUsername(username);
        temp.setPassword(newPassword);

        User oldUser = userMapper.selectByUsername(username);

        if (Objects.isNull(oldUser)) {
            throw new BusinessException("用户不存在");
        }

        if (Objects.equals(oldUser.getPassword(), temp.getPassword())) {
            throw new BusinessException("新密码不能与旧密码相同");
        }

        return userMapper.updatePassword(temp) > 0;
    }

    public boolean deleteByUsername(String username) {
        return userMapper.deleteByUsername(username) > 0;
    }

    public boolean updateAvatar(String username, String avatar) {
        return userMapper.updateAvatar(username, avatar) > 0;
    }

    public boolean updateIntroduction(String username, String introduction) {
        return userMapper.updateIntroduction(username, introduction) > 0;
    }

    public boolean updateNickname(String username, String nickname) {
        return userMapper.updateNickname(username, nickname) > 0;
    }
}
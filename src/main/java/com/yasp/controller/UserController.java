package com.yasp.controller;

import com.yasp.dto.LoginRequest;
import com.yasp.entity.User;
import com.yasp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController // 1. 标记这是一个Rest接口控制器，返回JSON（不渲染页面）
@RequestMapping("/api/user") // 2. 统一设定本类下所有接口的URL前缀
public class UserController {
    @Autowired // 3. 自动注入Service层对象
    private UserService userService;

    @GetMapping("/{username}")
    // 4. HTTP GET，路径变量，查询指定用户名用户
    public User getByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    @PostMapping("/register")
    // 5. HTTP POST, 用于注册。@RequestBody自动把json反序列化为User对象
    public boolean register(@RequestBody User user) {
        return userService.register(user);
    }

    public Map<String, Object> login(@RequestBody LoginRequest req) {
        User user = userService.findByUsername(req.getUsername());

        if (user == null || !Objects.equals(user.getPassword(), getByUsername(req.getUsername()).getPassword())) {
            return Map.of("success", false, "message", "用户名或密码错误");
        }

        return Map.of();
    }

    @PutMapping("/update")
    // 6. HTTP PUT，更新用户除密码外信息
    public boolean updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/update-password")
    // 7. 修改密码，接收用户名和新密码
    public boolean updatePassword(@RequestParam String username,
                                  @RequestParam String newPassword) {
        return userService.updatePassword(username, newPassword);
    }

    @DeleteMapping("/{username}")
    // 8. 删除指定用户名用户
    public boolean deleteByUsername(@PathVariable String username) {
        return userService.deleteByUsername(username);
    }

    @PutMapping("/update-avatar")
    // 9. 更新头像
    public boolean updateAvatar(@RequestParam String username,
                                @RequestParam String avatar) {
        return userService.updateAvatar(username, avatar);
    }

    @PutMapping("/update-introduction")
    // 10. 更新个人简介
    public boolean updateIntroduction(@RequestParam String username,
                                      @RequestParam String introduction) {
        return userService.updateIntroduction(username, introduction);
    }

    @PutMapping("/update-nickname")
    // 11. 更新昵称
    public boolean updateNickname(@RequestParam String username,
                                  @RequestParam String nickname) {
        return userService.updateNickname(username, nickname);
        }
}
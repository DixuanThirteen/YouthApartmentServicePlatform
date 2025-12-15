package com.yasp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    /**
     * 密码加密器：BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * 开发阶段：先把所有请求放行，不使用 Spring Security 自带登录
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 关掉 CSRF，方便你用 Postman/前端调试接口
                .csrf(AbstractHttpConfigurer::disable)
                // 所有请求都不需要认证，全部放行
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        // 如果你暂时不需要默认登录页、登出等，也可以不配置 formLogin/logout
        return http.build();
    }
}

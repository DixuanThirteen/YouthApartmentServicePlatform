package com.yasp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class JwtTokenService {

    private final Set<String> blacklist = new HashSet<>(); // 存储注销的令牌
    /**
     * 将令牌加入黑名单
     */
    public boolean invalidateToken(String token) {
        try {
            // 验证令牌是否有效
            boolean isValid = validateToken(token);
            if (isValid) {
                blacklist.add(token); // 加入黑名单
                return true;
            }
        } catch (Exception e) {
            log.error("Failed to invalidate token: " + token, e);
            return false;
        }
        return false;
    }

    /**
     * 验证令牌是否有效
     */
    private boolean validateToken(String token) {
        // 在实际环境中，可以通过签名验证令牌
        // 或解码令牌确保合法性
        return true;
    }

    /**
     * 检查令牌是否已注销
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklist.contains(token);
    }
}

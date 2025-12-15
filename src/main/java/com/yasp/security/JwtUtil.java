package com.yasp.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    // 建议用至少 256-bit 的秘钥（Base64 编码后长度大概 43+ 字符）
    // 你可以用在线工具生成随机 32 字节，然后 Base64 一下
    private static final String SECRET_KEY_BASE64 = "uI+JpT2YEL0n2GcA1DF3oU7b1fCiXy2vZzM3S5n9c2k=";

    // 解析成 Key 对象
    private static Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY_BASE64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 根据自定义过期时间生成 token（毫秒）
    public static String generateToken(Long userId, String username, String role, long expMillis) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expMillis);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析 token，返回 Claims
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
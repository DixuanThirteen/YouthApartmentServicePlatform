package com.yasp.security;

import com.yasp.service.JwtTokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/admins",
            "/admins/login",
            "/providers/login",
            "/users/login",
            "/providers",
            "/users"
    );

    // 通过构造器注入 jwtTokenService
    public JwtAuthFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    //指定无需过滤路径
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return PUBLIC_PATHS.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse resp,
            FilterChain chain
    ) throws IOException, ServletException {
        //测试用
//        System.out.println("URI=" + req.getRequestURI() + ", Authorization=" + req.getHeader("Authorization"));

        //获取 Authorization 头
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            chain.doFilter(req, resp);
            return;
        }

        String token = authHeader.substring(7);// 去掉 "Bearer " 前缀

        // 检查令牌是否被列入黑名单
        if (jwtTokenService.isTokenBlacklisted(token)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("Token has been blacklisted");
            return; // 停止后续过滤，直接返回响应
        }

        try {
            Claims claims = JwtUtil.parseToken(token);

            Long userId = Long.valueOf(claims.getSubject());
            String username = (String) claims.get("username");
            String role = (String) claims.get("role");

            // 写到 request attribute
            req.setAttribute("userId", userId);
            req.setAttribute("username", username);
            req.setAttribute("role", role);

            // 写入 Spring Security 上下文（Principal 才能取到）
            // 写入 SecurityContext，Controller 才能用 Principal
            var authorities = role == null
                    ? List.<SimpleGrantedAuthority>of()
                    : List.of(new SimpleGrantedAuthority("ROLE_" + role));

            var authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(req, resp);
            //从这开始注释
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("Invalid or expired token");
        }
            //以下测试用，先注释上面----^
//        } catch (Exception e) {
//            e.printStackTrace();
//            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            resp.getWriter().write(
//                    "Invalid or expired token: " + e.getClass().getName() + " - " + e.getMessage()
//            );
//        }
    }
}
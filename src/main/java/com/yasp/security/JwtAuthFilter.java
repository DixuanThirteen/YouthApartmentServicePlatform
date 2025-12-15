package com.yasp.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JwtAuthFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI();

        // 登录、注册等接口允许匿名访问
        if (path.startsWith("/api/auth/login") ||
                path.startsWith("/api/user/register")) {
            chain.doFilter(request, response);
            return;
        }

        // 从 Authorization 头中获取 token
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7); // 去掉 "Bearer "

        try {
            Claims claims = JwtUtil.parseToken(token);
            // 这里你可以把 userId/username 放到 request attribute 或 ThreadLocal
            req.setAttribute("userId", Long.valueOf(claims.getSubject()));
            req.setAttribute("username", claims.get("username"));
            req.setAttribute("role", claims.get("role"));

            chain.doFilter(request, response);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("Invalid or expired token");
        }
    }
}
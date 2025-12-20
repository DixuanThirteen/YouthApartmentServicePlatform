package com.yasp.security;

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

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/admins/login",
            "/providers/login",
            "/users/login",
            "/providers",
            "/users"
    );

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
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 你目前是“缺 token 就 401”
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = JwtUtil.parseToken(token);

            Long userId = Long.valueOf(claims.getSubject());
            String username = (String) claims.get("username");
            String role = (String) claims.get("role");

            // 1) 仍然可选：写到 request attribute（给你自己用）
            req.setAttribute("userId", userId);
            req.setAttribute("username", username);
            req.setAttribute("role", role);

            // 2) 关键：写入 Spring Security 上下文（Principal 才能取到）
            // 写入 SecurityContext，Controller 才能用 Principal
            var authorities = role == null
                    ? List.<SimpleGrantedAuthority>of()
                    : List.of(new SimpleGrantedAuthority("ROLE_" + role));

            var authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(req, resp);
            //从这开始注释
//        } catch (Exception e) {
//            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            resp.getWriter().write("Invalid or expired token");
//        }
            //以下测试用，先注释上面----^
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(
                    "Invalid or expired token: " + e.getClass().getName() + " - " + e.getMessage()
            );
        }
    }
}
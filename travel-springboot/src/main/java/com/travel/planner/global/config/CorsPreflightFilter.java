package com.travel.planner.global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * preflight(OPTIONS) 요청을 Security 체인보다 먼저 처리해 CORS 헤더를 붙임.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsPreflightFilter extends OncePerRequestFilter {

    private static final String ALLOWED_ORIGIN = "http://localhost:5173";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        // credentials 사용 시 * 불가 → Authorization, Content-Type 등 명시
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, Origin, X-Requested-With");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        filterChain.doFilter(request, response);
    }
}

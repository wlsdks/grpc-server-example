package com.demo.grpc.config.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
//@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 요청 URL, 메서드, Authorization 헤더 로깅
        String authHeader = request.getHeader("Authorization");
        log.info("Request URL: {}", request.getRequestURL());
        log.info("Request Method: {}", request.getMethod());
        log.info("Authorization Header: {}", authHeader);

        // 모든 헤더 로깅
        Collections.list(request.getHeaderNames()).forEach(headerName ->
                log.info("Header '{}': {}", headerName, request.getHeader(headerName))
        );

        filterChain.doFilter(request, response);
    }

}
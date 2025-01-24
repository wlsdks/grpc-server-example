package com.demo.grpc.config.security.filter;

import com.demo.grpc.config.security.common.ServerTokenUtil;
import com.demo.grpc.config.security.server.ServerTokenClaims;
import com.demo.grpc.config.security.service.ServerAuthenticationService;
import com.demo.grpc.exception.ServerAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class ServerAuthenticationFilter extends OncePerRequestFilter {

    private final ServerTokenUtil serverTokenUtil;
    private final ServerAuthenticationService serverAuthenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader("Server-Authorization");

        if (authHeader == null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            if (!authHeader.startsWith("Server ")) {
                throw new ServerAuthenticationException("Invalid server authentication header");
            }

            String token = authHeader.substring(7);
            ServerTokenClaims claims = serverTokenUtil.validateAndGetClaims(token);
            Authentication authentication = serverAuthenticationService.authenticateServer(claims);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        } catch (ServerAuthenticationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
        }
    }

}
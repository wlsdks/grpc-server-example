package com.demo.grpc.config.security;

import com.demo.grpc.config.security.filter.JwtAuthenticationFilter;
import com.demo.grpc.config.security.filter.RequestLoggingFilter;
import com.demo.grpc.config.security.service.JwtAuthenticationService;
import com.demo.grpc.config.security.server.ServerTokenClaims;
import com.demo.grpc.config.security.service.ServerAuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationService jwtAuthenticationService;
    private final ServerAuthenticationService serverAuthenticationService;

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> {
            // 서버 인증 토큰인 경우
            if (authentication.getPrincipal() instanceof ServerTokenClaims) {
                return serverAuthenticationService.authenticateServer((ServerTokenClaims) authentication.getPrincipal());
            }

            // 사용자 JWT 토큰인 경우
            if (authentication.getPrincipal() instanceof String token) {
                return jwtAuthenticationService.authenticateToken(token);
            }

            throw new AuthenticationServiceException("Unsupported authentication type");
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/members/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new RequestLoggingFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage()))
                )
                .build();
    }

}

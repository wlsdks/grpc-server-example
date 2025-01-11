package com.demo.grpc.config.security.grpc;

import com.demo.grpc.config.security.common.JwtAuthenticationService;
import com.demo.grpc.exception.JwtAuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.security.authentication.BearerAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.CompositeGrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

// gRPC Request → GrpcAuthenticationReader → JwtAuthenticationService  
//→ JwtUtil(토큰 검증) → CustomUserDetailsService(사용자 정보 조회) → SecurityContext  
@Slf4j
@RequiredArgsConstructor
@Configuration
public class GrpcJwtSecurityConfig {

    private final JwtAuthenticationService jwtAuthenticationService;

    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {
        List<GrpcAuthenticationReader> readers = new ArrayList<>();

        // Bearer 토큰을 처리할 수 있는 리더를 추가합니다  
        readers.add(new BearerAuthenticationReader(token -> {
            try {
                log.info("토큰을 받음 - 토큰: {}", token);
                Authentication auth = jwtAuthenticationService.authenticateToken(token);

                // 명시적으로 인증 성공 여부 확인  
                if (auth != null && auth.isAuthenticated()) {
                    return auth;
                } else {
                    return null;
                }
            } catch (JwtAuthenticationException e) {
                log.info("토큰 인증 실패: {}", e.getMessage());
                return null;
            }
        }));

        // 모든 리더를 하나의 복합 리더로 결합합니다  
        return new CompositeGrpcAuthenticationReader(readers);
    }

}
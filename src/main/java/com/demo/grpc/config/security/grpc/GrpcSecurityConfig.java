package com.demo.grpc.config.security.grpc;

import com.demo.grpc.config.security.common.JwtAuthenticationService;
import com.demo.grpc.exception.JwtAuthenticationException;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.security.authentication.BearerAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.CompositeGrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

// gRPC Request → GrpcAuthenticationReader → JwtAuthenticationService
//→ JwtUtil(토큰 검증) → CustomUserDetailsService(사용자 정보 조회) → SecurityContext
@RequiredArgsConstructor
@Configuration
public class GrpcSecurityConfig {

    private final JwtAuthenticationService jwtAuthenticationService;

    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {
        List<GrpcAuthenticationReader> readers = new ArrayList<>();

        // Bearer 토큰을 처리할 수 있는 리더를 추가합니다
        readers.add(new BearerAuthenticationReader(token -> {
            try {
                // JWT 토큰을 검증하고 Authentication 객체를 생성합니다
                return jwtAuthenticationService.authenticateToken(token);
            } catch (JwtAuthenticationException e) {
                // 인증 실패 시 null을 반환하여 인증 실패를 표시합니다
                return null;
            }
        }));

        // 모든 리더를 하나의 복합 리더로 결합합니다
        return new CompositeGrpcAuthenticationReader(readers);
    }

}
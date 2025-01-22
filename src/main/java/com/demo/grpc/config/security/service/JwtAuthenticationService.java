package com.demo.grpc.config.security.service;

import com.demo.grpc.config.security.common.JwtUtil;
import com.demo.grpc.config.security.token.GrpcAuthenticationToken;
import com.demo.grpc.exception.JwtAuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtAuthenticationService {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public Authentication authenticateToken(String token) {
        try {
            log.info("JWT 인증 시작 - 토큰: {}", token);

            // 토큰 유효성 검사
            if (!jwtUtil.isTokenValid(token)) {
                log.warn("JWT 토큰이 유효하지 않음");
                throw new JwtAuthenticationException("유효하지 않은 JWT 토큰");
            }

            // 사용자 이름 추출
            String username = jwtUtil.extractUsername(token);
            log.info("JWT에서 추출한 사용자 이름: {}", username);

            // 사용자 정보 로드
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            log.info("사용자 정보 로드 완료 - 권한: {}", userDetails.getAuthorities());

            return new GrpcAuthenticationToken(
                    userDetails,
                    token,
                    userDetails.getAuthorities()
            );
        } catch (JwtAuthenticationException e) {
            log.error("JWT 인증 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("JWT 인증 중 알 수 없는 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("JWT 인증 중 오류 발생", e);
        }
    }

}
package com.demo.grpc.config.security.common;

import com.demo.grpc.exception.JwtAuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    /**
     * @param token JWT 토큰
     * @return Authentication 객체
     * @apiNote JWT 토큰을 검증하고 Authentication 객체를 생성합니다.
     */
    public Authentication authenticateToken(String token) {
        log.info("토큰 인증 시작 - 토큰: {}", token);

        try {
            if (!jwtUtil.isTokenValid(token)) {
                log.info("토큰 유효성 검증 실패");
                throw new JwtAuthenticationException("Invalid JWT token");
            }
            log.info("토큰 유효성 검증 성공");

            String username = jwtUtil.extractUsername(token);
            log.info("토큰에서 추출한 사용자명: {}", username);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            log.info("사용자 정보 로드 성공: {}", userDetails.getUsername());

            return new UsernamePasswordAuthenticationToken(
                    userDetails,
                    token,
                    userDetails.getAuthorities()
            );
        } catch (Exception e) {
            log.info("인증 과정 중 예외 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

}
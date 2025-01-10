package com.demo.grpc.config.security.common;

import com.demo.grpc.exception.JwtAuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

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
        if (!jwtUtil.isTokenValid(token)) {
            throw new JwtAuthenticationException("Invalid JWT token");
        }

        String username = jwtUtil.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // 토큰을 credentials에 포함시킵니다
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                token,  // 토큰을 credentials로 저장
                userDetails.getAuthorities()
        );
    }

}
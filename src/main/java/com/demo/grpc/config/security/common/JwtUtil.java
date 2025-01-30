package com.demo.grpc.config.security.common;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // 상수로 시크릿 키 문자열 정의
    private static final String SECRET_KEY_STRING = "your_very_long_and_secure_secret_key_at_least_256_bits_long_for_hs256_algorithm";
    private SecretKey secretKey;
    private final long expiration = 3600000; // 1시간

    @PostConstruct
    public void init() {
        // 문자열에서 SecretKey 생성
        secretKey = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());
    }

    // JWT 토큰 생성
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    // 토큰에서 사용자 이름 추출
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 토큰 유효성 검증
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}

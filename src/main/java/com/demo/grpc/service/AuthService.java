package com.demo.grpc.service;

import com.demo.grpc.config.security.common.JwtUtil;
import com.demo.grpc.dto.response.LoginResponse;
import com.demo.grpc.entity.MemberEntity;
import com.demo.grpc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    /**
     * @param email    이메일
     * @param password 비밀번호
     * @return 로그인 응답
     * @apiNote 이메일과 비밀번호로 로그인합니다.
     */
    public LoginResponse login(String email, String password) {
        // 회원 조회
        MemberEntity memberEntity = memberRepository.findByEmail(email)
                .filter(member -> member.getPassword().equals(password))
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // JWT 토큰 생성
        String token = jwtUtil.generateToken(memberEntity.getEmail());

        return LoginResponse.of(token, "Bearer");
    }

}
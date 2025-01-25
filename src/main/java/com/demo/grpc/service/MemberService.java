package com.demo.grpc.service;

import com.demo.grpc.config.security.common.JwtUtil;
import com.demo.grpc.dto.response.LoginResponse;
import com.demo.grpc.dto.request.MemberSignUpRequestDTO;
import com.demo.grpc.dto.response.ResponseMemberDTO;
import com.demo.grpc.entity.MemberEntity;
import com.demo.grpc.mapper.MemberMapper;
import com.demo.grpc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final JwtUtil jwtUtil;

    /**
     * @param memberDTO 회원 가입 요청
     * @return 회원 가입 응답
     * @apiNote 회원을 생성합니다.
     */
    @Transactional
    public ResponseMemberDTO createMember(MemberSignUpRequestDTO memberDTO) {
        MemberEntity memberEntity = memberMapper.dtoToEntity(memberDTO);
        MemberEntity savedMemberEntity = memberRepository.save(memberEntity);
        return memberMapper.entityToDto(savedMemberEntity);
    }

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

    /**
     * @param memberId 회원 ID
     * @return 회원 조회 응답
     * @apiNote 회원 ID로 회원을 조회합니다.
     */
    public ResponseMemberDTO getMemberById(Long memberId) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        return memberMapper.entityToDto(memberEntity);
    }

}
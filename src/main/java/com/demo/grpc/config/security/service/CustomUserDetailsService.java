package com.demo.grpc.config.security.service;

import com.demo.grpc.entity.MemberEntity;
import com.demo.grpc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * 사용자 이름을 기반으로 사용자 정보를 조회합니다.
     *
     * @param username 사용자 이름
     * @return UserDetails 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        return createTestUserDetails(username);

//        return memberRepository.findByEmail(username)
//                .map(this::createUserDetails)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private UserDetails createUserDetails(MemberEntity member) {
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .authorities(Set.of(new SimpleGrantedAuthority("ROLE_USER")))
                .enabled(true)
                .build();
    }

    private UserDetails createTestUserDetails(String username) {
        return User.builder()
                .username(username)
                .password("") // 클라이언트에서는 비밀번호 검증이 필요 없음
                .authorities(Set.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }

}
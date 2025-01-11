package com.demo.grpc.config.security.service;

import com.demo.grpc.entity.MemberEntity;
import com.demo.grpc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return memberRepository.findByEmail(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private UserDetails createUserDetails(MemberEntity member) {
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .authorities(Set.of(new SimpleGrantedAuthority("ROLE_USER")))
                .enabled(true)
                .build();
    }

}
package com.demo.grpc.config.security.service;

import com.demo.grpc.entity.MemberEntity;
import com.demo.grpc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.debug("Loading user details for username: {}", username);

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
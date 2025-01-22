package com.demo.grpc.config.security.token;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class GrpcAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public GrpcAuthenticationToken(Object principal,
                                   Object credentials,
                                   Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        // 수동 호출 방지
        throw new IllegalArgumentException("setAuthenticated() 호출 금지 - 생성자를 통해 설정하십시오.");
    }

}

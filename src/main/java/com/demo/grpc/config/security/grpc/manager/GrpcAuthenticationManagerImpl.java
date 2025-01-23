package com.demo.grpc.config.security.grpc.manager;

import com.demo.grpc.config.security.server.ServerTokenClaims;
import com.demo.grpc.config.security.service.ServerAuthenticationService;
import com.demo.grpc.exception.JwtAuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Primary
@Slf4j
@RequiredArgsConstructor
@Component
public class GrpcAuthenticationManagerImpl implements AuthenticationManager {

    private final ServerAuthenticationService serverAuthenticationService;

    /**
     * @param authentication 인증 객체
     * @return 인증된 객체
     * @throws AuthenticationException 인증 예외
     * @apiNote 스프링 시큐리티를 위한 gRPC 인증 처리를 수행합니다.
     * gRPC 인터셉터 내부의 interceptCall 메서드가 호출되면서 사용됩니다. (두번째 try-catch 블록)
     * 이 클래스가 선언되어 있어야만 gRPC 인터셉터 내부의 interceptCall 메서드가 동작할때 "Unsupported authentication Type" 오류가 발생하지 않습니다.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            log.debug("Authenticating user: {}", authentication.getName());

            if (authentication.getPrincipal() instanceof ServerTokenClaims) {
                // 이미 앞단계(gRPC 인터셉터)에서 인증된 경우에는 바로 통과
                return authentication;
            } else {
                // 서버 토큰을 이용한 인증 처리
                return serverAuthenticationService.authenticateServer((ServerTokenClaims) authentication.getPrincipal());
            }
        } catch (JwtAuthenticationException e) {
            log.error("JWT 인증 실패: {}", e.getMessage());
            throw new BadCredentialsException("JWT 인증 실패", e);
        } catch (Exception e) {
            log.error("인증 처리 중 예상치 못한 오류 발생", e);
            throw new AuthenticationServiceException("인증 처리 중 오류 발생", e);
        }
    }

}
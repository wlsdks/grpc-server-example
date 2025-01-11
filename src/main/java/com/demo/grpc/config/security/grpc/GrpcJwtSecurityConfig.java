package com.demo.grpc.config.security.grpc;

import com.demo.grpc.config.security.common.JwtAuthenticationService;
import com.demo.grpc.exception.JwtAuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerSecurityAutoConfiguration;
import net.devh.boot.grpc.server.security.authentication.BearerAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.CompositeGrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@EnableAutoConfiguration(exclude = {GrpcServerSecurityAutoConfiguration.class})
@Slf4j
@RequiredArgsConstructor
@Configuration
public class GrpcJwtSecurityConfig {

    private final JwtAuthenticationService jwtAuthenticationService;

    /**
     * @return gRPC 인증 리더
     * @apiNote gRPC 서버에서 사용할 인증 리더를 빈으로 등록합니다.
     * 이 Reader를 빈으로 등록하면 gRPC 요청이 발생하면 내부적으로 동작하는 인터셉터에서 호출되어 jwt를 추출하여 인증을 수행합니다.
     * gRPC 인터셉터 내부의 interceptCall 메서드가 호출되면서 사용됩니다. (첫번째 try-catch 블록)
     */
    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {
        List<GrpcAuthenticationReader> readers = new ArrayList<>();

        // Bearer 토큰을 처리할 수 있는 리더를 추가합니다
        readers.add(new BearerAuthenticationReader(token -> {
            try {
                log.info("GrpcAuthenticationReader 토큰을 받음 - 토큰: {}", token);
                return jwtAuthenticationService.authenticateToken(token);
            } catch (JwtAuthenticationException e) {
                log.info("토큰 인증 실패: {}", e.getMessage());
                return null;
            }
        }));

        // 모든 리더를 하나의 복합 리더로 결합합니다
        return new CompositeGrpcAuthenticationReader(readers);
    }

}
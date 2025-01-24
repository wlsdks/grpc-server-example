package com.demo.grpc.config.security.grpc;

import com.demo.grpc.config.security.common.ServerTokenUtil;
import com.demo.grpc.config.security.server.ServerTokenClaims;
import com.demo.grpc.config.security.service.JwtAuthenticationService;
import com.demo.grpc.config.security.service.ServerAuthenticationService;
import io.grpc.Metadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.security.authentication.CompositeGrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class GrpcServerTokenInterceptor {

    private final JwtAuthenticationService jwtAuthenticationService;
    private final ServerTokenUtil serverTokenUtil;
    private final ServerAuthenticationService serverAuthenticationService;

    private static final Metadata.Key<String> SERVER_AUTH_KEY =
            Metadata.Key.of("Server-Authorization", Metadata.ASCII_STRING_MARSHALLER);

    /**
     * @return gRPC 인증 리더
     * @apiNote gRPC 서버에서 사용할 인증 리더를 빈으로 등록합니다.
     * 이 Reader를 빈으로 등록하면 gRPC 요청이 발생하면 내부적으로 동작하는 인터셉터에서 호출되어 jwt를 추출하여 인증을 수행합니다.
     * gRPC 인터셉터 내부의 interceptCall 메서드가 호출되면서 사용됩니다. (첫번째 try-catch 블록)
     */
    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {
        List<GrpcAuthenticationReader> readers = new ArrayList<>();

        // 서버 토큰 처리를 위한 커스텀 리더
        readers.add((context, headers) -> {
            try {
                String authHeader = headers.get(SERVER_AUTH_KEY);
                if (authHeader == null || !authHeader.startsWith("Server ")) {
                    log.debug("서버 인증 헤더가 없거나 잘못된 형식입니다");
                    return null;
                }

                String token = authHeader.substring(7);
                ServerTokenClaims claims = serverTokenUtil.validateAndGetClaims(token);
                log.trace("서버 토큰 인증 시도 - 클레임: {}", claims);

                return serverAuthenticationService.authenticateServer(claims);
            } catch (Exception e) {
                log.info("서버 토큰 인증 실패: {}", e.getMessage());
                return null;
            }
        });

        return new CompositeGrpcAuthenticationReader(readers);
    }

}
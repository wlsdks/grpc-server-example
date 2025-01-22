package com.demo.grpc.config.security.service;

import com.demo.grpc.config.security.token.ServerAuthenticationToken;
import com.demo.grpc.config.security.server.ServerProperties;
import com.demo.grpc.config.security.server.ServerTokenClaims;
import com.demo.grpc.config.security.server.ServerType;
import com.demo.grpc.exception.ServerAuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@RequiredArgsConstructor
@Service
public class ServerAuthenticationService {

    private final ServerProperties serverProperties;

    public Authentication authenticateServer(ServerTokenClaims claims) {
        // 1. 서버 타입 유효성 검증
        if (!isValidServerType(claims.serverType())) {
            throw new ServerAuthenticationException("Invalid server type: " + claims.serverType());
        }

        // 2. 기본적인 claims 검증
        validateServerClaims(claims);

        // 3. 서버 타입별 특수 검증
        if (isCurrentServer(ServerType.CLIENT_SERVER)) {
            validateClientServerRequest(claims);
        } else if (isCurrentServer(ServerType.AUTH_SERVER)) {
            validateAuthServerRequest(claims);
        }

        // 4. 인증 객체 생성 및 반환
        return new ServerAuthenticationToken(claims);
    }

    // 현재 서버의 타입을 확인하는 메소드
    private boolean isCurrentServer(ServerType type) {
        return serverProperties.getType() == type;
    }

    private void validateClientServerRequest(ServerTokenClaims claims) {
        // 클라이언트 서버에서의 특정 검증 로직
        if (claims.serverType() != ServerType.AUTH_SERVER) {
            throw new ServerAuthenticationException("Client server can only accept requests from Auth server");
        }
    }

    private void validateAuthServerRequest(ServerTokenClaims claims) {
        // 인증 서버에서의 특정 검증 로직
        if (claims.serverType() != ServerType.CLIENT_SERVER) {
            throw new ServerAuthenticationException("Auth server can only accept requests from Client server");
        }
    }

    private boolean isValidServerType(ServerType serverType) {
        return serverType != null && Arrays.asList(
                ServerType.CLIENT_SERVER,
                ServerType.AUTH_SERVER
        ).contains(serverType);
    }

    private void validateServerClaims(ServerTokenClaims claims) {
        // serverId가 null이거나 비어있지 않은지 확인
        if (claims.serverId() == null || claims.serverId().trim().isEmpty()) {
            throw new ServerAuthenticationException("Server ID is required");
        }

        // 여기에 추가적인 검증 로직 추가
        // 예: 허용된 서버 ID 목록과 대조
        // 예: 서버 상태 확인
        // 예: 서버 권한 레벨 확인
    }

}
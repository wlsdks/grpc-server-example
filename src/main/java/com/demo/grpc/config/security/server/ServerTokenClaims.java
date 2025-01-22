package com.demo.grpc.config.security.server;

public record ServerTokenClaims(
        ServerType serverType,
        String serverId
) {
    // 생성자에서 기본 검증
    public ServerTokenClaims {
        if (serverType == null) {
            throw new IllegalArgumentException("Server type cannot be null");
        }
        if (serverId == null || serverId.trim().isEmpty()) {
            throw new IllegalArgumentException("Server ID cannot be null or empty");
        }
        serverId = serverId.trim(); // 정규화
    }
}
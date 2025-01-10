package com.demo.grpc.config.security.common;

import io.grpc.Metadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class TokenExtractor {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final Metadata.Key<String> AUTHORIZATION_METADATA_KEY =
            Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    // HTTP 요청에서 토큰 추출
    public Optional<String> extractFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return Optional.of(authorizationHeader.substring(BEARER_PREFIX.length()));
        }
        return Optional.empty();
    }

    // gRPC 메타데이터에서 토큰 추출
    public Optional<String> extractFromMetadata(Metadata metadata) {
        String authorizationHeader = metadata.get(AUTHORIZATION_METADATA_KEY);
        return extractFromHeader(authorizationHeader);
    }

}
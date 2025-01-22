package com.demo.grpc.config.security.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ServerType {

    AUTH_SERVER("AUTH_SERVER", "인증 서버"),
    CLIENT_SERVER("CLIENT_SERVER", "클라이언트 서버");

    private final String code;
    private final String description;

}
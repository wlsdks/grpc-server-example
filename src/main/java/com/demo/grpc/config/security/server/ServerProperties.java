package com.demo.grpc.config.security.server;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@ConfigurationProperties(prefix = "app.server")  // application.yml의 'server' 프리픽스와 매칭
public class ServerProperties {

    private final ServerType type;  // server.type과 자동으로 매핑됨
    private final String id;  // server.id와 매핑됨

    // 생성자 바인딩 사용
    public ServerProperties(ServerType type, String id) {
        this.type = type;
        this.id = id;
    }

}
package com.demo.grpc.config.server;

import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatServerConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addConnectorCustomizers(connector -> {
            // Tomcat 10 이상 또는 최신 JDK에서는 아래와 같이 cast할 수 있습니다.
            if (connector.getProtocolHandler() instanceof Http11NioProtocol protocol) {
                protocol.setMaxThreads(200);        // 동시 요청을 감당할 최대 스레드 수
                protocol.setMinSpareThreads(20);      // 유휴 상태에서도 유지할 최소 스레드 수
                protocol.setConnectionTimeout(30000); // 연결 타임아웃 (30초)
            }
        });
    }

}
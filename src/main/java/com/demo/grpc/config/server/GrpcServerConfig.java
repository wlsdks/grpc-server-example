package com.demo.grpc.config.server;

import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
public class GrpcServerConfig {

    @Bean
    public GrpcServerConfigurer grpcServerConfigurer() {
        return serverBuilder -> {
            if (serverBuilder instanceof NettyServerBuilder nettyServerBuilder) {
                // 기본 이벤트 루프 스레드 외에 blocking 작업 처리를 위한 별도 Executor 사용
                int availableProcessors = Runtime.getRuntime().availableProcessors();
                // blocking 작업이 많은 경우 CPU 코어 수의 2배 정도의 스레드를 사용
                ExecutorService blockingExecutor = Executors.newFixedThreadPool(availableProcessors * 2);

                nettyServerBuilder
                        .executor(blockingExecutor)
                        .maxInboundMessageSize(10 * 1024 * 1024) // 예: 최대 10MB 메시지
                        .keepAliveTime(30, TimeUnit.SECONDS)
                        .keepAliveTimeout(5, TimeUnit.SECONDS)
                        .permitKeepAliveWithoutCalls(true);
            }
        };
    }

}
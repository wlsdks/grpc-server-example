package com.demo.grpc.config.server;

import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
public class GrpcServerConfig {

    @Bean
    public GrpcServerConfigurer keepAliveServerConfigurer() {
        return serverBuilder -> {
            if (serverBuilder instanceof NettyServerBuilder) {
                // 기본 ExecutorService 생성
                ExecutorService executorService = Executors.newFixedThreadPool(50);

                // SecurityContext를 전파하는 ExecutorService로 래핑
                ExecutorService securityContextExecutorService =
                        new DelegatingSecurityContextExecutorService(executorService);

                ((NettyServerBuilder) serverBuilder)
                        .executor(securityContextExecutorService) // 보안 컨텍스트를 전파하는 실행자 사용
                        .maxInboundMessageSize(10 * 1024 * 1024)
                        .keepAliveTime(30, TimeUnit.SECONDS)
                        .keepAliveTimeout(5, TimeUnit.SECONDS)
                        .permitKeepAliveWithoutCalls(true);
            }
        };
    }


    // 스레드 오류 상황을 재현하고 싶다면 이 메서드를 사용하세요.
//    @Bean
//    public GrpcServerConfigurer keepAliveServerConfigurer() {
//        return serverBuilder -> {
//            if (serverBuilder instanceof NettyServerBuilder) {
////                // 기본 ExecutorService 생성
////                ExecutorService executorService = Executors.newFixedThreadPool(50);
////
////                // SecurityContext를 전파하는 ExecutorService로 래핑
////                ExecutorService securityContextExecutorService =
////                        new DelegatingSecurityContextExecutorService(executorService);
//
//                ((NettyServerBuilder) serverBuilder)
//                        .executor(Executors.newFixedThreadPool(50)) // 보안 컨텍스트를 전파하는 실행자 사용
//                        .maxInboundMessageSize(10 * 1024 * 1024)
//                        .keepAliveTime(30, TimeUnit.SECONDS)
//                        .keepAliveTimeout(5, TimeUnit.SECONDS)
//                        .permitKeepAliveWithoutCalls(true);
//            }
//        };

}
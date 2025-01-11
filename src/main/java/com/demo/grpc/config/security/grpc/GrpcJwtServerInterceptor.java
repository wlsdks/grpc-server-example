package com.demo.grpc.config.security.grpc;

import com.demo.grpc.config.security.common.JwtAuthenticationService;
import com.demo.grpc.config.security.common.TokenExtractor;
import com.demo.grpc.exception.JwtAuthenticationException;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerSecurityAutoConfiguration;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@EnableAutoConfiguration(exclude = {GrpcServerSecurityAutoConfiguration.class})
@Slf4j
@GrpcGlobalServerInterceptor
@RequiredArgsConstructor
public class GrpcJwtServerInterceptor implements ServerInterceptor {

    private final TokenExtractor tokenExtractor;
    private final JwtAuthenticationService jwtAuthenticationService;

    /**
     * 서버 호출을 인터셉트하여 JWT 토큰을 인증합니다.
     *
     * @param call    ServerCall
     * @param headers Metadata
     * @param next    ServerCallHandler
     * @param <ReqT>  요청 타입
     * @param <RespT> 응답 타입
     * @return ServerCall.Listener
     */
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                                                                 Metadata headers,
                                                                 ServerCallHandler<ReqT, RespT> next) {
        // 현재 스레드를 로깅한다.
        log.info("현재 스레드 인터셉터: {}", Thread.currentThread().getName());

        // Authorization 헤더 추출
        String authorizationHeader = headers.get(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER));
        log.info("gRPC 서버 인터셉터에서 수신한 Authorization 헤더: {}", authorizationHeader);

        // 인증 헤더가 없는 경우
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            log.error("인증 헤더가 없습니다.");
            return closeCallWithError(call, Status.UNAUTHENTICATED.withDescription("인증 헤더가 필요합니다."));
        }

        // 토큰 추출 및 인증
        try {
            return tokenExtractor.extractFromHeader(authorizationHeader)
                    .map(token -> authenticateAndProceed(token, call, headers, next))
                    .orElseGet(() -> closeCallWithError(call,
                            Status.UNAUTHENTICATED.withDescription("유효한 토큰이 없습니다.")));
        } catch (Exception e) {
            log.error("인증 처리 중 오류 발생: {}", e.getMessage(), e);
            return closeCallWithError(call, Status.INTERNAL.withDescription("인증 처리 중 오류가 발생했습니다."));
        }
    }

    /**
     * JWT 토큰을 인증하고 요청을 처리합니다.
     *
     * @param token   JWT 토큰
     * @param call    ServerCall
     * @param headers Metadata
     * @param next    ServerCallHandler
     * @param <ReqT>  요청 타입
     * @param <RespT> 응답 타입
     * @return ServerCall.Listener
     */
    private <ReqT, RespT> ServerCall.Listener<ReqT> authenticateAndProceed(String token,
                                                                           ServerCall<ReqT, RespT> call,
                                                                           Metadata headers,
                                                                           ServerCallHandler<ReqT, RespT> next) {
        try {
            Authentication auth = jwtAuthenticationService.authenticateToken(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("JWT 인증 성공 - 사용자: {}", auth.getName());
            return next.startCall(new SimpleForwardingServerCall<>(call) {
            }, headers);
        } catch (JwtAuthenticationException e) {
            log.error("JWT 인증 실패: {}", e.getMessage());
            return closeCallWithError(call,
                    Status.UNAUTHENTICATED.withDescription(e.getMessage()));
        }
    }

    /**
     * 서버 호출을 오류로 종료합니다.
     *
     * @param call    ServerCall
     * @param status  상태
     * @param <ReqT>  요청 타입
     * @param <RespT> 응답 타입
     * @return ServerCall.Listener
     */
    private <ReqT, RespT> ServerCall.Listener<ReqT> closeCallWithError(ServerCall<ReqT, RespT> call,
                                                                       Status status) {
        call.close(status, new Metadata());
        return new ServerCall.Listener<ReqT>() {
        };
    }

}
package com.demo.grpc.config.security.grpc;

import com.demo.grpc.config.security.server.ServerTokenClaims;
import com.demo.grpc.config.security.common.ServerTokenUtil;
import com.demo.grpc.config.security.service.ServerAuthenticationService;
import com.demo.grpc.exception.ServerAuthenticationException;
import io.grpc.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GrpcServerAuthenticationServerInterceptor implements ServerInterceptor {

    private final ServerTokenUtil serverTokenUtil;
    private final ServerAuthenticationService serverAuthenticationService;

    private static final Metadata.Key<String> SERVER_AUTH_KEY =
            Metadata.Key.of("Server-Authorization", Metadata.ASCII_STRING_MARSHALLER);

    /**
     * @apiNote gRPC 요청으로 받은 Server-Authorization 헤더를 검증하고, 인증된 서버인지 확인하는 인터셉터
     */
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String authHeader = headers.get(SERVER_AUTH_KEY);
        if (authHeader == null || !authHeader.startsWith("Server ")) {
            call.close(Status.UNAUTHENTICATED.withDescription("Missing server authentication"), headers);
            return new ServerCall.Listener<ReqT>() {
            };
        }

        try {
            String token = authHeader.substring(7);
            ServerTokenClaims claims = serverTokenUtil.validateAndGetClaims(token);
            serverAuthenticationService.authenticateServer(claims);

            return next.startCall(call, headers);
        } catch (ServerAuthenticationException e) {
            call.close(Status.UNAUTHENTICATED.withDescription(e.getMessage()), headers);
            return new ServerCall.Listener<ReqT>() {
            };
        }
    }

}
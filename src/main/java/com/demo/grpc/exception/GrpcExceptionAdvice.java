package com.demo.grpc.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GrpcExceptionAdvice {

    // 일반적인 유효하지 않은 인자 예외 처리
    @GrpcExceptionHandler(IllegalArgumentException.class)
    public Status handleInvalidArgument(IllegalArgumentException e) {
        return Status.INVALID_ARGUMENT
                .withDescription("Invalid argument provided: " + e.getMessage());
    }

    // 리소스를 찾을 수 없는 경우를 위한 별도의 커스텀 예외 클래스 사용
    @GrpcExceptionHandler(ResourceNotFoundException.class)
    public StatusRuntimeException handleResourceNotFoundException(ResourceNotFoundException e) {
        return Status.NOT_FOUND
                .withDescription("Resource not found: " + e.getMessage())
                .asRuntimeException();
    }

    // JWT 관련 예외 처리
    @GrpcExceptionHandler(JwtAuthenticationException.class)
    public Status handleJwtAuthenticationException(JwtAuthenticationException e) {
        return Status.UNAUTHENTICATED
                .withDescription("Authentication failed: " + e.getMessage());
    }

    // 기타 예기치 않은 예외 처리
    @GrpcExceptionHandler(Exception.class)
    public Status handleException(Exception e) {
        return Status.INTERNAL
                .withDescription("An unexpected error occurred: " + e.getMessage());
    }

}
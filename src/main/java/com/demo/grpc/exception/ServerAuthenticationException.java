package com.demo.grpc.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ServerAuthenticationException extends RuntimeException {

    private final String message;

}

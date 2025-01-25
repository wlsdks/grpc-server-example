package com.demo.grpc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponse {

    private String accessToken;
    private String tokenType;

    public static LoginResponse of(String token, String bearer) {
        return new LoginResponse(token, bearer);
    }

}

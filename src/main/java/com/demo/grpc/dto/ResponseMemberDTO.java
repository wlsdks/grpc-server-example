package com.demo.grpc.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ResponseMemberDTO {

    private Long id;
    private String email;
    private String password;
    private String name;
    private String profileImageBase64;
    private String etcInfo;

}
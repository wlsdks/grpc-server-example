package com.demo.grpc.dto;

import lombok.Data;

@Data
public class MemberSignUpRequestDTO {

    private Long id;
    private String email;
    private String password;
    private String name;
    private String profileImageBase64;
    private String etcInfo;

}

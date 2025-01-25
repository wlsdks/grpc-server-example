package com.demo.grpc.dto.request;

import lombok.Data;

import java.util.Set;

@Data
public class MemberSignUpRequestDTO {

    private String email;
    private String password;
    private String name;
    private String profileImageBase64;
    private AddressDTO address;
    private ContactDTO contact;
    private Set<String> interests;
    private Set<String> skills;
    private String metadata;

}
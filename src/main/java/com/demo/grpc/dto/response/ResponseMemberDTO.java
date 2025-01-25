package com.demo.grpc.dto.response;

import com.demo.grpc.dto.request.AddressDTO;
import com.demo.grpc.dto.request.ContactDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public class ResponseMemberDTO {

    private Long id;
    private String email;
    private String name;
    private String profileImageBase64;
    private AddressDTO address;
    private ContactDTO contact;
    private Set<String> interests;
    private Set<String> skills;
    private String metadata;

}
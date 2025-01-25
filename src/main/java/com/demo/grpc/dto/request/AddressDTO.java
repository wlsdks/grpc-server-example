package com.demo.grpc.dto.request;

import lombok.Data;

import java.util.Map;

@Data
public class AddressDTO {

    private String street;
    private String city;
    private String country;
    private String postalCode;
    private Map<String, String> additionalInfo;

}

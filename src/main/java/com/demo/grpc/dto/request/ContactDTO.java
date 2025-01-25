package com.demo.grpc.dto.request;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ContactDTO {

    private String phone;
    private String mobile;
    private String workPhone;
    private List<String> emails;
    private Map<String, String> socialMedia;

}
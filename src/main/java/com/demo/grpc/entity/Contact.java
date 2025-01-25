package com.demo.grpc.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@Embeddable
public class Contact {

    private String phone;
    private String mobile;
    private String workPhone;

    @ElementCollection
    private List<String> emails;

    @ElementCollection
    private Map<String, String> socialMedia;

}
package com.demo.grpc.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@Embeddable
public class Address {

   private String street;
   private String city; 
   private String country;
   private String postalCode;

   @ElementCollection(fetch = FetchType.EAGER)
   private Map<String, String> additionalInfo;

}

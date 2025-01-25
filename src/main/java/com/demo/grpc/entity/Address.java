package com.demo.grpc.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Map;

@Getter
@Embeddable
public class Address {

   private String street;
   private String city; 
   private String country;
   private String postalCode;
   
   @ElementCollection
   private Map<String, String> additionalInfo;

}

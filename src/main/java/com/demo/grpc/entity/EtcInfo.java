package com.demo.grpc.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Embeddable
public class EtcInfo {

    @Embedded
    private Address address;

    @Embedded
    private Contact contact;

    @ElementCollection
    @CollectionTable(name = "member_interests")
    private Set<String> interests = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "member_skills")
    @Column(name = "skill")
    private Set<String> skills = new HashSet<>();

    @Column(columnDefinition = "json")
    private String metadata; // JSON 형태로 저장

}
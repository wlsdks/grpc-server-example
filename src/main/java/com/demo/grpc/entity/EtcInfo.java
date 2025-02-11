package com.demo.grpc.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Embeddable
public class EtcInfo {

    @Embedded
    private Address address;

    @Embedded
    private Contact contact;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "member_interests")
    private Set<String> interests = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "member_skills")
    @Column(name = "skill")
    private Set<String> skills = new HashSet<>();

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private String metadata; // JSON 형태로 저장

}
package com.demo.grpc.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "member")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column
    private String password;

    @Column
    private String name;

    // 대용량 필드
    @Column(columnDefinition = "TEXT")
    private String profileImageBase64;

//    @Embedded
//    private EtcInfo etcInfo;

    public void changeProfileImageBase64(String dummyBase64) {
        this.profileImageBase64 = dummyBase64;
    }

}
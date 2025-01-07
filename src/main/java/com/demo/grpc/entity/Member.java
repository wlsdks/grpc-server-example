package com.demo.grpc.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "member")
public class Member {

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

    @Column(columnDefinition = "TEXT")
    private String etcInfo;

    // change 메서드
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void changeProfileImage(String newImage) {
        this.profileImageBase64 = newImage;
    }

    public void changeEtcInfo(String newInfo) {
        this.etcInfo = newInfo;
    }

}
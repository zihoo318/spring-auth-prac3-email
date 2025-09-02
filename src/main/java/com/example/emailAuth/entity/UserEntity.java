package com.example.emailAuth.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor // 기본 생성자 자동 생성 어노테이션
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    @Column(nullable=false, unique=true, length=190)
    private String email;
    private boolean enabled = false; // 이메일 인증 전엔 false
    private String role = "USER";

    @Builder
    public UserEntity(String username, String password,String email, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }
}

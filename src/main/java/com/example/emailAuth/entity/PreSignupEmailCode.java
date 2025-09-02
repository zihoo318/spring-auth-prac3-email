package com.example.emailAuth.entity;


import jakarta.persistence.*;
import lombok.Getter; import lombok.Setter;
import java.time.Instant;

@Entity
@Getter @Setter
@Table
public class PreSignupEmailCode {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=190)
    private String email;         // normalized (lowercase)

    // 클라이언트에 주는 건 짧은 code 서버엔 해시 저장
    @Column(nullable=false, length=60)
    private String codeHash;      // BCrypt 등으로 저장

    @Column(nullable=false)
    private Instant expiresAt; // 만료시간

    private Instant verifiedAt;   // 검증 성공 시각

    @Column(nullable=false)
    private Instant createdAt = Instant.now(); // 발급 시간
}
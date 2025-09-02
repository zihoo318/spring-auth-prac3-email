package com.example.emailAuth.entity.repository;

import com.example.emailAuth.entity.PreSignupEmailCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface PreSignupEmailCodeRepository extends JpaRepository<PreSignupEmailCode, Long> {
    // 최근 발급된 레코드 1개 (검증/만료 확인용)
    Optional<PreSignupEmailCode> findTopByEmailOrderByCreatedAtDesc(String email);

    // 최근 N시간/분 내에 verifiedAt 기록이 있는지
    boolean existsByEmailAndVerifiedAtAfter(String email, Instant after);

    // 가입 완료 후 깨끗이 정리
    void deleteByEmail(String email);
}
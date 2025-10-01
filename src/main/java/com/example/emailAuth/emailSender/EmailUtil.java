package com.example.emailAuth.emailSender;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class EmailUtil {
    private final EmailProperties emailProperties;

    public EmailUtil(EmailProperties emailProperties) {
        this.emailProperties = emailProperties;
    }

    // 허용 도메인 화이트리스트 검사
    public boolean isAllowedDomain(String email) {
        var list = emailProperties.getAllowedDomains();
        if (list == null || list.isEmpty()) return true; // 제한 없음
        String domain = email.substring(email.indexOf('@') + 1).toLowerCase();
        return list.stream().anyMatch(d -> domain.equals(d.trim().toLowerCase()));
    }

    // 이메일 저장 형식
    public String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    public String generateNumericCode(int len) {
        SecureRandom r = new SecureRandom();
        StringBuilder sb = new StringBuilder(len);
        for (int i=0; i<len; i++) sb.append(r.nextInt(10));
        return sb.toString();
    }

//    // 안전한 이메일 인증 토큰 생성
//    public String generateToken() {
//        byte[] bytes = new byte[32];
//        new java.security.SecureRandom().nextBytes(bytes);
//        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
//    }

//    // 인증 링크 생성 (verifyBaseUrl + token)
//    public String buildVerifyLink(String token) {
//        return emailProperties.getVerifyBaseUrl() + "?token=" + token;
//    }
}

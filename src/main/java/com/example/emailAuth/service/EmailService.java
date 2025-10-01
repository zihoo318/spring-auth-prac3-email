package com.example.emailAuth.service;


import com.example.emailAuth.emailSender.EmailProperties;
import com.example.emailAuth.emailSender.EmailUtil;
import com.example.emailAuth.entity.PreSignupEmailCode;
import com.example.emailAuth.entity.repository.PreSignupEmailCodeRepository;
import com.example.emailAuth.entity.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final PreSignupEmailCodeRepository preRepo;
    private final EmailProperties emailProperties;
    private final EmailUtil emailUtil;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine; // Thymeleaf
    private final PasswordEncoder passwordEncoder;     // code 해시 저장용

    // 인증 코드 발송
    public void sendEmailCode(String email) throws MessagingException {
        email = emailUtil.normalize(email);
        if (!emailUtil.isAllowedDomain(email)) {
            throw new IllegalArgumentException("허용되지 않은 이메일 도메인입니다.");
        }

        String code = emailUtil.generateNumericCode(6); // ex) 6자리 숫자
        String codeHash = passwordEncoder.encode(code);

        // 인증번호 db에 저장
        PreSignupEmailCode rec = new PreSignupEmailCode();
        rec.setEmail(email);
        rec.setCodeHash(codeHash);
        rec.setExpiresAt(Instant.now().plusSeconds(emailProperties.getTokenExpireMinutes() * 60L));
        preRepo.save(rec);

        // 메일 템플릿에 코드 표시
        sendCodeEmailHtml(email, code);
    }

    // HTML 인증 메일 발송 (내부 사용)
    private void sendCodeEmailHtml(String to, String code) throws MessagingException {
        Context ctx = new Context();
        ctx.setVariable("code", code);
        ctx.setVariable("appName", "이메일 인증 테스트");
        // 템플릿
        String html = templateEngine.process("emailTemplates", ctx);

        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper h = new MimeMessageHelper(mime, true, "UTF-8");
        h.setTo(to);
        h.setSubject("이메일 인증 코드 안내");

        String plain = """
                안녕하세요.

                아래 인증 코드를 화면에 입력해주세요.

                인증 코드: %s

                유효시간 내에 입력하지 않으면 만료됩니다.
                """.formatted(code);

        h.setText(plain, html);
        mailSender.send(mime);
    }

    // 인증 코드 검증
    public boolean verifyEmailCode(String email, String code) {
        email = emailUtil.normalize(email);
        var recOpt = preRepo.findTopByEmailOrderByCreatedAtDesc(email);
        if (recOpt.isEmpty()) return false;

        var rec = recOpt.get();

        // 만료/시도 제한
        if (rec.getVerifiedAt() != null) return true; // 이미 성공한 기록
        if (rec.getExpiresAt().isBefore(Instant.now())) return false;

        // 해시 비교
        boolean ok = passwordEncoder.matches(code, rec.getCodeHash());
        if (ok) rec.setVerifiedAt(Instant.now());
        preRepo.save(rec);
        return ok;
    }


    // 메일 링크 클릭 후 토큰 검증
//    public boolean verifyToken(String token) {
//        EmailTokenEntity emailToken = tokenRepo.findByToken(token).orElse(null);
//        if (emailToken == null) return false;
//        if (emailToken.isUsed()) return false;
//        if (emailToken.getExpiresAt().isBefore(Instant.now())) return false;
//
//        // 토큰 사용 처리
//        emailToken.setUsed(true);
//        tokenRepo.save(emailToken);
//
//        // 유저 활성화 처리 (현재 엔티티는 enabled 플래그)
//        UserEntity user = emailToken.getUser();
//        user.setEnabled(true);
//        userRepo.save(user);
//
//        return true;
//    }
}
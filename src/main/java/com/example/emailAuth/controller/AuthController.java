package com.example.emailAuth.controller;

import com.example.emailAuth.emailSender.dto.VerifyCodeRequest;
import com.example.emailAuth.entity.UserEntity;
import com.example.emailAuth.service.AuthService;
import com.example.emailAuth.service.EmailService;
import com.example.emailAuth.service.dto.LoginRequest;
import com.example.emailAuth.service.dto.LoginResponse;
import com.example.emailAuth.service.dto.SignupRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private  final AuthService authService;
    private final EmailService emailService;

    public AuthController(AuthService authService, EmailService emailService) {
        this.authService = authService;
        this.emailService = emailService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserEntity> signUp(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signUp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse token = authService.login(request);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", token.refreshToken())
                .httpOnly(true).secure(false).path("/").sameSite("Lax")
                .maxAge(1000 * 60 * 2) // 쿠키 유효 단위 = 1분
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString()) // Refresh 토큰은 쿠키에 Set-Cookie 헤더로 전달
                .body(new LoginResponse(token.accessToken(), null, "두 토큰 모두 재발급"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@CookieValue("refreshToken") String refreshToken,
                                                 HttpServletResponse response) {
        return ResponseEntity.ok(authService.refreshAccessToken(refreshToken, response)); // new 토큰 반환
    }

    @PostMapping("/email/send-code") // 인증 코드 발송
    public ResponseEntity<String> sendCode(@RequestParam String email) throws Exception {
        try {
            emailService.sendEmailCode(email);
            return ResponseEntity.ok("인증 코드를 발송했습니다.");
        } catch (IllegalArgumentException e) { // 허용되지 않은 도메인 등
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/email/verify-code") // 인증 결과 반환
    public ResponseEntity<?> verifyCode(@RequestBody VerifyCodeRequest req) {
        boolean ok = emailService.verifyEmailCode(req.email(), req.code());
        return ok ? ResponseEntity.ok().body("{\"verified\":true}")
                : ResponseEntity.badRequest().body("{\"verified\":false}");
    }
}

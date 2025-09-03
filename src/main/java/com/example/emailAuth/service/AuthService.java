package com.example.emailAuth.service;


import com.example.emailAuth.entity.UserEntity;
import com.example.emailAuth.entity.repository.PreSignupEmailCodeRepository;
import com.example.emailAuth.entity.repository.UserRepository;
import com.example.emailAuth.security.JwtUtil;
import com.example.emailAuth.service.dto.LoginRequest;
import com.example.emailAuth.service.dto.LoginResponse;
import com.example.emailAuth.service.dto.SignupRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final PreSignupEmailCodeRepository preRepo;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil,
                       PasswordEncoder passwordEncoder, PreSignupEmailCodeRepository preRepo) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.preRepo = preRepo;
    }

    @Transactional // 회원가입 전체를 하나의 트랜잭션으로
    public UserEntity signUp(SignupRequest request){
        final String username = request.userName().trim();
        final String email    = request.email().trim().toLowerCase();

        if (userRepository.findByUsername(username).isPresent()) throw new RuntimeException("이미 존재하는 username 입니다.");
        if (userRepository.findByEmail(email).isPresent())       throw new RuntimeException("이미 사용 중인 이메일입니다.");

        // 최근 5분 내 검증 성공 기록이 있어야 가입 허용
        boolean verified = preRepo.existsByEmailAndVerifiedAtAfter(
                email, Instant.now().minusSeconds(5 * 60));
        if (!verified) throw new RuntimeException("이메일 인증을 먼저 완료해주세요.");

        String encodedPw = passwordEncoder.encode(request.password());
        UserEntity user = UserEntity.builder()
                .username(username)
                .password(encodedPw)
                .email(email)
                .role(request.role())
                .build();
        user.setEnabled(true); // 사전 인증 완료 사용자로 바로 활성화
        UserEntity saved = userRepository.save(user);

        // 동일 이메일의 사전 인증 레코드 정리
        preRepo.deleteByEmail(email);
        return saved;
    }

    public LoginResponse login(LoginRequest request){
        UserEntity user = userRepository.findByUsername(request.userName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 이메일 인증(활성화) 여부 확인
        if (!user.isEnabled()) {
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) { // 해시 비교 matches()
            throw new RuntimeException("Invalid password"); // 비번 틀린 경우
        }

        String accessToken = jwtUtil.generateToken(user.getUsername(),user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        return new LoginResponse(accessToken,refreshToken, null); // 로그인 성공 시 토큰 둘다 발급
    }

    public LoginResponse refreshAccessToken(String refreshToken, HttpServletResponse response){
        if (!jwtUtil.validateToken(refreshToken)) { // 유효하지 않은 경우
            // 브라우저의 refresh 쿠키 삭제
            Cookie cookie = new Cookie("refreshToken", null);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(0); // 만료 처리
            response.addCookie(cookie);

            throw new RuntimeException("Invalid refresh token"); // 글로벌 예외 처리로 401 반환 가능
        }

        String username = jwtUtil.extractUsername(refreshToken);
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtUtil.generateToken(user.getUsername(), user.getRole()); // 토큰 새로 생성

        return new LoginResponse(newAccessToken, refreshToken, "refresh로 access 재발급");
    }

}

package com.example.emailAuth.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 기본 strength 10
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtUtil jwtUtil) throws Exception {
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtUtil);

        http
                // 기본 보안 설정
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스 & 루트 허용
                        .requestMatchers("/", "/index.html").permitAll()
                        // auth 관련은 인증 없이 허용 (회원가입,로그인 진행해야 토큰 있으니까 토큰 인증 필요 없음)
                        .requestMatchers("/auth/**").permitAll()
                        // 권한 확인은 메서드(@PreAuthorize("hasRole('ADMIN')"))로하고 인증이 필요하도록만 만들어두기
                        .requestMatchers("/role/**").authenticated()
                        .requestMatchers( // swagger
                                "/v3/api-docs/**",
                                "/v3/api-docs/swagger-config",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // 메일 인증
                        .requestMatchers("/auth/email", "/auth/email/request").permitAll()
                        // 그 외는 인증 필요
                        .anyRequest().authenticated()
                )
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() { return new JwtAuthFilter(jwtUtil); }

}

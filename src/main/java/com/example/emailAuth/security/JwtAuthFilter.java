package com.example.emailAuth.security;

import com.example.emailAuth.security.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // 스웨거 & 오픈API 문서
        if (path.startsWith("/v3/api-docs") ||
                path.equals("/v3/api-docs/swagger-config") ||
                path.equals("/swagger-ui.html") ||
                path.startsWith("/swagger-ui")) {
            return true;
        }

        // 인증 없이 열어둘 엔드포인트들
        if (path.startsWith("/auth/") ||
                path.equals("/") ||
                path.equals("/index.html") ||
                path.equals("/favicon.ico")) {
            return true;
        }

        // CORS preflight 는 토큰 검사 스킵
//        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//            return true;
//        }

        return false; // 나머지는 필터 적용(토큰 검사)
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, jakarta.servlet.ServletException {

        String authHeader = request.getHeader("Authorization");

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new JwtAuthException("AUTHORIZATION_MISSING", "Access token is missing");
            }

            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                throw new JwtAuthException("AUTHORIZATION_INVALID", "Invalid access token");
            }

            String username = jwtUtil.extractUsername(token);
            String role = "ROLE_" + jwtUtil.extractRole(token); //hasRole("ADMIN")은 "ROLE_ADMIN"을 찾음
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role)); // 권한 객체에 넣기

            // Authentication 객체 생성 (username + authorities)
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            // SecurityContext에 등록
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response); // 다음으로 넘어가기

        } catch (JwtAuthException ex) {
            sendError(response, ex);
        }
    }

    private void sendError(HttpServletResponse response, JwtAuthException ex) throws IOException {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(401, ex.getCode(), ex.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
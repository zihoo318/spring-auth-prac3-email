package com.example.emailAuth.service;

import com.example.emailAuth.entity.RoleResponse;
import com.example.emailAuth.entity.UserEntity;
import com.example.emailAuth.entity.repository.UserRepository;
import com.example.emailAuth.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public RoleService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public RoleResponse process(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return new RoleResponse(user.getUsername(), user.getRole());
    }
}

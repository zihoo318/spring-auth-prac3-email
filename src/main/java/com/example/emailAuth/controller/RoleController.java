package com.example.emailAuth.controller;


import com.example.emailAuth.entity.RoleResponse;
import com.example.emailAuth.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@EnableMethodSecurity
@RequestMapping("/role")
public class RoleController {
    private final RoleService roleService;
    public RoleController(RoleService roleService) {this.roleService = roleService;}


    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponse> admin(Principal principal) {
        String username = principal.getName(); // JWTAuthFilter가 넣어준 인증 객체에서 추출
        return ResponseEntity.ok(roleService.process(username));
    }

    @PostMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RoleResponse> user(Principal principal) {
        String username = principal.getName(); // JWTAuthFilter가 넣어준 인증 객체에서 추출
        return ResponseEntity.ok(roleService.process(username));
    }
}


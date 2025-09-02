package com.example.emailAuth.security.dto;

public record ErrorResponse(
        int status,
        String code,
        String message
) {}

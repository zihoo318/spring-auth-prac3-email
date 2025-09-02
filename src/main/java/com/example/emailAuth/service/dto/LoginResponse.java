package com.example.emailAuth.service.dto;

public record LoginResponse(String accessToken, String refreshToken, String logMessage) {}

package com.example.emailAuth.emailSender.dto;

public record VerifyCodeRequest(String email, String code) {}

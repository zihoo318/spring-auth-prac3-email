package com.example.emailAuth.service.dto;

public record SignupRequest(String userName, String password, String role, String email) {}

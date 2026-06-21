package com.example.carrentalapp.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val confirmPassword: String
)

data class AuthResponse(
    val token: String,
    val email: String,
    val role: String,
    val userId: String
)

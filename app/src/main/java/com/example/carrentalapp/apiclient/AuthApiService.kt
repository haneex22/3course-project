package com.example.carrentalapp.apiclient

import com.example.carrentalapp.model.AuthResponse
import com.example.carrentalapp.model.LoginRequest
import com.example.carrentalapp.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Void>
}

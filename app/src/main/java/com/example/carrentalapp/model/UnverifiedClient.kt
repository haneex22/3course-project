package com.example.carrentalapp.model

data class UnverifiedClient(
    val userId: String,
    val email: String,
    val passportSeries: String?,
    val passportNumber: String?,
    val licenseNumber: String?,
    val registrationDate: String
)

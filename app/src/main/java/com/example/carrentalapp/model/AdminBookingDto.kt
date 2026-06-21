package com.example.carrentalapp.model

data class AdminBookingDto(
    val id: String,
    val carId: String?,
    val carModelName: String,
    val clientEmail: String,
    val clientId: String,
    val startDateTime: String,
    val endDateTime: String,
    val status: String,
    val amount: Double,
    val currency: String,
    val createdAt: String
)

package com.example.carrentalapp.model

data class BookingRequest(
    val carId: String,
    val startDateTime: String,
    val endDateTime: String
)

data class ReservationDto(
    val id: String,
    val carId: String?,
    val carModelName: String,
    val startDateTime: String,
    val endDateTime: String,
    val status: String,
    val amount: Double,
    val currency: String,
    val createdAt: String
)

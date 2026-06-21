package com.example.carrentalapp.model

data class AdminCarRequest(
    val vin: String,
    val licensePlate: String,
    val modelName: String,
    val carClass: String,
    val baseDailyRate: Double,
    val imageUrl: String?
)

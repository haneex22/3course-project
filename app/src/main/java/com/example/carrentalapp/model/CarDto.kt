package com.example.carrentalapp.model

import java.util.UUID

data class CarDto(
    val id: String,
    val modelName: String,
    val carClass: String,
    val baseDailyRate: Double,
    val status: String,
    val imageUrl: String?,
    val licensePlate: String,
    val vin: String
)

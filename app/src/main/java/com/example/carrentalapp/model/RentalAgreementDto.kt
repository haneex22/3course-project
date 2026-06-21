package com.example.carrentalapp.model

data class RentalAgreementDto(
    val id: String,
    val reservationId: String?,
    val agreementNumber: String,
    val signedAt: String,
    val initialMileage: Long,
    val initialFuelLevel: Int,
    val finalMileage: Long,
    val finalFuelLevel: Int,
    val active: Boolean,
    val carModelName: String?,
    val clientEmail: String?
)

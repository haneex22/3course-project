package com.example.carrentalapp.localcache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "booking_cache")
data class BookingCacheEntity(
    @PrimaryKey val id: String,
    val carId: String?,
    val carModelName: String,
    val startDateTime: String,
    val endDateTime: String,
    val status: String,
    val amount: Double,
    val currency: String,
    val createdAt: String
)

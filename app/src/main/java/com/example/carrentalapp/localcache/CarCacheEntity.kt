package com.example.carrentalapp.localcache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "car_cache")
data class CarCacheEntity(
    @PrimaryKey val id: String,
    val modelName: String,
    val carClass: String,
    val baseDailyRate: Double,
    val status: String,
    val imageUrl: String?,
    val licensePlate: String,
    val vin: String
)

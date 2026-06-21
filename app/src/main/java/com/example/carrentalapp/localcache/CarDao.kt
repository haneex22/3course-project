package com.example.carrentalapp.localcache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CarDao {
    @Query("SELECT * FROM car_cache")
    suspend fun getAllCars(): List<CarCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cars: List<CarCacheEntity>)

    @Query("DELETE FROM car_cache")
    suspend fun clearAll()
}

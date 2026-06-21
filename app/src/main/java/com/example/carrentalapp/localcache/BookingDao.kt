package com.example.carrentalapp.localcache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookingDao {
    @Query("SELECT * FROM booking_cache ORDER BY createdAt DESC")
    suspend fun getAllBookings(): List<BookingCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bookings: List<BookingCacheEntity>)

    @Query("DELETE FROM booking_cache")
    suspend fun clearAll()

    @Query("SELECT * FROM booking_cache WHERE id = :id")
    suspend fun getBookingById(id: String): BookingCacheEntity?
}

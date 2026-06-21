package com.example.carrentalapp.apiclient

import com.example.carrentalapp.model.BookingRequest
import com.example.carrentalapp.model.ReservationDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BookingApiService {
    @POST("api/v1/bookings")
    suspend fun createBooking(@Body request: BookingRequest): Response<ReservationDto>

    @GET("api/v1/bookings/my")
    suspend fun getMyBookings(): Response<List<ReservationDto>>

    @GET("api/v1/bookings/{id}")
    suspend fun getBookingById(@Path("id") id: String): Response<ReservationDto>

    @POST("api/v1/bookings/{id}/cancel")
    suspend fun cancelBooking(@Path("id") id: String): Response<Void>
}

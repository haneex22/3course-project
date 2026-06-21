package com.example.carrentalapp.apiclient

import com.example.carrentalapp.model.AdminBookingDto
import com.example.carrentalapp.model.AdminCarRequest
import com.example.carrentalapp.model.BusyPeriod
import com.example.carrentalapp.model.CarDto
import com.example.carrentalapp.model.CarStatusUpdateRequest
import com.example.carrentalapp.model.HandoverRequest
import com.example.carrentalapp.model.RentalAgreementDto
import com.example.carrentalapp.model.ReturnRequest
import com.example.carrentalapp.model.UnverifiedClient
import retrofit2.Response
import retrofit2.http.*

interface CarApiService {
    @GET("api/v1/cars")
    suspend fun getCars(
        @Query("carClass") carClass: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<List<CarDto>>

    @GET("api/v1/cars/{id}")
    suspend fun getCarById(@Path("id") id: String): Response<CarDto>

    @GET("api/v1/cars/{id}/busy")
    suspend fun getBusyPeriods(@Path("id") id: String): Response<List<BusyPeriod>>

    @PUT("api/v1/cars/{id}/status")
    suspend fun updateCarStatus(
        @Path("id") id: String,
        @Body request: CarStatusUpdateRequest
    ): Response<CarDto>

    @GET("api/v1/admin/cars")
    suspend fun getAllCarsAdmin(): Response<List<CarDto>>

    @POST("api/v1/admin/cars")
    suspend fun addCar(@Body request: AdminCarRequest): Response<CarDto>

    @GET("api/v1/admin/clients/unverified")
    suspend fun getUnverifiedClients(): Response<List<UnverifiedClient>>

    @PUT("api/v1/admin/clients/{userId}/verify")
    suspend fun verifyClient(@Path("userId") userId: String): Response<Void>

    @PUT("api/v1/admin/cars/{id}")
    suspend fun updateCar(
        @Path("id") id: String,
        @Body request: AdminCarRequest
    ): Response<CarDto>

    @DELETE("api/v1/admin/cars/{id}")
    suspend fun deleteCar(@Path("id") id: String): Response<Void>

    @GET("api/v1/admin/bookings")
    suspend fun getAllBookingsAdmin(): Response<List<AdminBookingDto>>

    @GET("api/v1/admin/bookings/{id}")
    suspend fun getBookingByIdAdmin(@Path("id") id: String): Response<AdminBookingDto>

    @POST("api/v1/admin/bookings/{id}/cancel")
    suspend fun cancelBookingAdmin(@Path("id") id: String): Response<Void>

    @POST("api/v1/admin/bookings/{id}/handover")
    suspend fun handoverCar(
        @Path("id") id: String,
        @Body request: HandoverRequest
    ): Response<RentalAgreementDto>

    @POST("api/v1/admin/bookings/{id}/return")
    suspend fun returnCar(
        @Path("id") id: String,
        @Body request: ReturnRequest
    ): Response<RentalAgreementDto>
}

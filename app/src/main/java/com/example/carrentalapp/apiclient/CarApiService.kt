package com.example.carrentalapp.apiclient

import com.example.carrentalapp.model.AdminCarRequest
import com.example.carrentalapp.model.BusyPeriod
import com.example.carrentalapp.model.CarDto
import com.example.carrentalapp.model.CarStatusUpdateRequest
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
}

package com.example.carrentalapp.apiclient

import android.content.Context
import com.example.carrentalapp.apiclient.interceptors.JwtInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Use 10.0.2.2 for Android Emulator to reach localhost
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private lateinit var retrofit: Retrofit

    fun init(context: Context) {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(JwtInterceptor())
            .addInterceptor(logging)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApiService by lazy { retrofit.create(AuthApiService::class.java) }
    val carApi: CarApiService by lazy { retrofit.create(CarApiService::class.java) }
    val bookingApi: BookingApiService by lazy { retrofit.create(BookingApiService::class.java) }
}

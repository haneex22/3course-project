package com.example.carrentalapp.apiclient.interceptors

import com.example.carrentalapp.localcache.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response

class JwtInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = TokenStorage.token
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}

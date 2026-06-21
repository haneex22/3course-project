package com.example.carrentalapp

import android.app.Application
import com.example.carrentalapp.apiclient.ApiClient

class CarRentalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiClient.init(this)
    }
}

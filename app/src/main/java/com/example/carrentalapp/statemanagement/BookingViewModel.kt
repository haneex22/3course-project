package com.example.carrentalapp.statemanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carrentalapp.apiclient.ApiClient
import com.example.carrentalapp.model.BookingRequest
import com.example.carrentalapp.model.BusyPeriod
import com.example.carrentalapp.model.CarDto
import com.example.carrentalapp.model.ReservationDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

sealed class BookingUiState {
    object Idle : BookingUiState()
    object Loading : BookingUiState()
    data class Success(val reservation: ReservationDto) : BookingUiState()
    data class Error(val message: String) : BookingUiState()
}

class BookingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<BookingUiState>(BookingUiState.Idle)
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    private val _selectedCar = MutableStateFlow<CarDto?>(null)
    val selectedCar: StateFlow<CarDto?> = _selectedCar.asStateFlow()

    private val _estimatedCost = MutableStateFlow<Double?>(null)
    val estimatedCost: StateFlow<Double?> = _estimatedCost.asStateFlow()

    private val _busyPeriods = MutableStateFlow<List<BusyPeriod>>(emptyList())
    val busyPeriods: StateFlow<List<BusyPeriod>> = _busyPeriods.asStateFlow()

    fun selectCar(car: CarDto) {
        _selectedCar.value = car
    }

    fun loadBusyPeriods(carId: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.carApi.getBusyPeriods(carId)
                if (response.isSuccessful) {
                    _busyPeriods.value = response.body() ?: emptyList()
                }
            } catch (_: Exception) {
                _busyPeriods.value = emptyList()
            }
        }
    }

    fun calculateCost(startMs: Long, endMs: Long, dailyRate: Double) {
        val days = ((endMs - startMs) / (1000 * 60 * 60 * 24)).coerceAtLeast(1)
        _estimatedCost.value = BigDecimal(dailyRate * days)
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
    }

    fun createBooking(carId: String, startDateTime: String, endDateTime: String) {
        viewModelScope.launch {
            _uiState.value = BookingUiState.Loading
            try {
                val response = ApiClient.bookingApi.createBooking(
                    BookingRequest(carId, startDateTime, endDateTime)
                )
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = BookingUiState.Success(response.body()!!)
                } else {
                    val msg = when (response.code()) {
                        409 -> "Эти даты уже заняты, выберите другие"
                        403 -> "Подтвердите профиль, чтобы бронировать"
                        400 -> "Проверьте выбранные даты"
                        else -> "Не удалось забронировать (${response.code()})"
                    }
                    _uiState.value = BookingUiState.Error(msg)
                }
            } catch (e: Exception) {
                _uiState.value = BookingUiState.Error("Нет соединения с сервером")
            }
        }
    }

    fun resetState() {
        _uiState.value = BookingUiState.Idle
    }
}

package com.example.carrentalapp.statemanagement

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carrentalapp.apiclient.ApiClient
import com.example.carrentalapp.localcache.TokenStorage
import com.example.carrentalapp.model.ReservationDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val email: String = "",
    val role: String = "",
    val isVerified: Boolean = false,
    val reservations: List<ReservationDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun load(context: Context) {
        val email = TokenStorage.getEmail(context) ?: ""
        val role = TokenStorage.getRole(context) ?: ""
        _uiState.value = _uiState.value.copy(email = email, role = role, isLoading = true)

        viewModelScope.launch {
            try {
                val response = ApiClient.bookingApi.getMyBookings()
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        reservations = response.body() ?: emptyList(),
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to load bookings")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun cancelBooking(context: Context, reservationId: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.bookingApi.cancelBooking(reservationId)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(message = "Бронирование отменено")
                    load(context)
                } else {
                    val msg = when (response.code()) {
                        409 -> "Это бронирование нельзя отменить"
                        else -> "Не удалось отменить (${response.code()})"
                    }
                    _uiState.value = _uiState.value.copy(error = msg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Нет соединения с сервером")
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null, error = null)
    }

    fun logout(context: Context) {
        TokenStorage.clear(context)
    }
}

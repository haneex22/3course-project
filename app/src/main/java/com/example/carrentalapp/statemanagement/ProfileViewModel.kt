package com.example.carrentalapp.statemanagement

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carrentalapp.apiclient.ApiClient
import com.example.carrentalapp.localcache.AppDatabase
import com.example.carrentalapp.localcache.BookingCacheEntity
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
    val message: String? = null,
    val isFromCache: Boolean = false
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun load(context: Context) {
        val email = TokenStorage.getEmail(context) ?: ""
        val role = TokenStorage.getRole(context) ?: ""
        _uiState.value = _uiState.value.copy(email = email, role = role, isLoading = true, isFromCache = false)

        // ADMIN/MANAGER не могут просматривать бронирования через клиентский endpoint
        if (role in listOf("ADMIN", "MANAGER")) {
            _uiState.value = _uiState.value.copy(
                reservations = emptyList(),
                isLoading = false,
                isFromCache = false,
                error = null,
                message = "Управляйте бронированиями через панель администратора"
            )
            return
        }

        viewModelScope.launch {
            try {
                val response = ApiClient.bookingApi.getMyBookings()
                if (response.isSuccessful) {
                    val bookings = response.body() ?: emptyList()
                    updateCache(context, bookings)
                    _uiState.value = _uiState.value.copy(
                        reservations = bookings,
                        isLoading = false,
                        error = null,
                        isFromCache = false
                    )
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Сессия истекла, войдите заново"
                        403 -> "Недостаточно прав для просмотра бронирований"
                        500 -> "Ошибка сервера, попробуйте позже"
                        else -> "Ошибка загрузки бронирований (код ${response.code()})"
                    }
                    // 401/403 — проблемы с доступом, а не с сетью. Не падаем в кэш,
                    // а показываем ошибку. В кэш падаем только при 500+ или других кодах.
                    if (response.code() == 401 || response.code() == 403) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isFromCache = false,
                            error = errorMsg
                        )
                    } else {
                        loadFromCache(context, errorMsg)
                    }
                }
            } catch (e: Exception) {
                val errorMsg = when {
                    e.message?.contains("Unable to resolve host") == true ->
                        "Сервер недоступен. Проверьте подключение к интернету"
                    e.message?.contains("Connection refused") == true ->
                        "Сервер не отвечает. Убедитесь, что бэкенд запущен"
                    e.message?.contains("timeout") == true ||
                    e.message?.contains("timed out") == true ->
                        "Сервер не отвечает (таймаут)"
                    else -> "Нет соединения с сервером: ${e.message}"
                }
                loadFromCache(context, errorMsg)
            }
        }
    }

    private suspend fun loadFromCache(context: Context, apiError: String?) {
        val db = AppDatabase.getInstance(context)
        val cached = db.bookingDao().getAllBookings()
        val bookings = cached.map { it.toDto() }
        _uiState.value = _uiState.value.copy(
            reservations = bookings,
            isLoading = false,
            isFromCache = true,
            error = if (bookings.isEmpty()) (apiError ?: "Нет соединения, а кэш пуст") else null
        )
    }

    private suspend fun updateCache(context: Context, bookings: List<ReservationDto>) {
        val db = AppDatabase.getInstance(context)
        db.bookingDao().clearAll()
        db.bookingDao().insertAll(bookings.map { it.toCacheEntity() })
    }

    fun refresh(context: Context) {
        load(context)
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
                        401 -> "Сессия истекла, войдите заново"
                        403 -> "Недостаточно прав"
                        409 -> "Это бронирование нельзя отменить"
                        else -> "Не удалось отменить (код ${response.code()})"
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

private fun BookingCacheEntity.toDto() = ReservationDto(
    id = id,
    carId = carId,
    carModelName = carModelName,
    startDateTime = startDateTime,
    endDateTime = endDateTime,
    status = status,
    amount = amount,
    currency = currency,
    createdAt = createdAt
)

private fun ReservationDto.toCacheEntity() = BookingCacheEntity(
    id = id,
    carId = carId,
    carModelName = carModelName,
    startDateTime = startDateTime,
    endDateTime = endDateTime,
    status = status,
    amount = amount,
    currency = currency,
    createdAt = createdAt
)

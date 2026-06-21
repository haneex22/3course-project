package com.example.carrentalapp.statemanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carrentalapp.apiclient.ApiClient
import com.example.carrentalapp.model.AdminBookingDto
import com.example.carrentalapp.model.AdminCarRequest
import com.example.carrentalapp.model.CarDto
import com.example.carrentalapp.model.CarStatusUpdateRequest
import com.example.carrentalapp.model.UnverifiedClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminUiState(
    val cars: List<CarDto> = emptyList(),
    val unverifiedClients: List<UnverifiedClient> = emptyList(),
    val bookings: List<AdminBookingDto> = emptyList(),
    val isLoading: Boolean = false,
    val isClientsLoading: Boolean = false,
    val isBookingsLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class AdminViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    fun loadAllCars() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = ApiClient.carApi.getAllCarsAdmin()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(cars = response.body()!!, isLoading = false)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Ошибка загрузки: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Ошибка сети")
            }
        }
    }

    fun loadUnverifiedClients() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isClientsLoading = true, error = null)
            try {
                val response = ApiClient.carApi.getUnverifiedClients()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(unverifiedClients = response.body()!!, isClientsLoading = false)
                } else {
                    _uiState.value = _uiState.value.copy(isClientsLoading = false, error = "Ошибка загрузки клиентов: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isClientsLoading = false, error = e.message ?: "Ошибка сети")
            }
        }
    }

    fun loadAllBookings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBookingsLoading = true, error = null)
            try {
                val response = ApiClient.carApi.getAllBookingsAdmin()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(bookings = response.body()!!, isBookingsLoading = false)
                } else {
                    val msg = when (response.code()) {
                        403 -> "Недостаточно прав для просмотра бронирований"
                        else -> "Ошибка загрузки бронирований (${response.code()})"
                    }
                    _uiState.value = _uiState.value.copy(isBookingsLoading = false, error = msg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isBookingsLoading = false, error = e.message ?: "Ошибка сети")
            }
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.carApi.cancelBookingAdmin(bookingId)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(successMessage = "Бронирование отменено")
                    loadAllBookings()
                } else {
                    val msg = when (response.code()) {
                        409 -> "Это бронирование нельзя отменить"
                        403 -> "Недостаточно прав"
                        else -> "Ошибка отмены (${response.code()})"
                    }
                    _uiState.value = _uiState.value.copy(error = msg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Ошибка сети")
            }
        }
    }

    fun verifyClient(userId: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.carApi.verifyClient(userId)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(successMessage = "Клиент верифицирован")
                    loadUnverifiedClients()
                } else {
                    val msg = when (response.code()) {
                        404 -> "Клиент не найден"
                        403 -> "Недостаточно прав"
                        else -> "Ошибка верификации (${response.code()})"
                    }
                    _uiState.value = _uiState.value.copy(error = msg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Ошибка сети")
            }
        }
    }

    fun updateCarStatus(carId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.carApi.updateCarStatus(carId, CarStatusUpdateRequest(newStatus))
                if (response.isSuccessful) {
                    loadAllCars()
                    _uiState.value = _uiState.value.copy(successMessage = "Статус обновлён")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Ошибка")
            }
        }
    }

    fun addCar(request: AdminCarRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = ApiClient.carApi.addCar(request)
                if (response.isSuccessful) {
                    loadAllCars()
                    _uiState.value = _uiState.value.copy(successMessage = "Автомобиль добавлен")
                } else {
                    val msg = when (response.code()) {
                        409 -> "Автомобиль с таким VIN или гос. номером уже существует"
                        400 -> "Проверьте правильность заполнения полей"
                        403 -> "Недостаточно прав для добавления"
                        else -> "Ошибка добавления (${response.code()})"
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false, error = msg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Ошибка сети")
            }
        }
    }

    fun updateCar(carId: String, request: AdminCarRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = ApiClient.carApi.updateCar(carId, request)
                if (response.isSuccessful) {
                    loadAllCars()
                    _uiState.value = _uiState.value.copy(successMessage = "Автомобиль обновлён")
                } else {
                    val msg = when (response.code()) {
                        409 -> "Автомобиль с таким VIN или гос. номером уже существует"
                        400 -> "Проверьте правильность заполнения полей"
                        403 -> "Недостаточно прав для редактирования"
                        else -> "Ошибка редактирования (${response.code()})"
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false, error = msg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Ошибка сети")
            }
        }
    }

    fun deleteCar(carId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = ApiClient.carApi.deleteCar(carId)
                if (response.isSuccessful) {
                    loadAllCars()
                    _uiState.value = _uiState.value.copy(successMessage = "Автомобиль удалён")
                } else {
                    val msg = when (response.code()) {
                        403 -> "Недостаточно прав для удаления"
                        404 -> "Автомобиль не найден"
                        409 -> "Не удалось удалить: авто имеет связанные данные"
                        else -> "Ошибка удаления (${response.code()})"
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false, error = msg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Ошибка сети")
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null, error = null)
    }
}

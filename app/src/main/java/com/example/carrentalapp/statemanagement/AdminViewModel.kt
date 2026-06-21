package com.example.carrentalapp.statemanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carrentalapp.apiclient.ApiClient
import com.example.carrentalapp.model.AdminCarRequest
import com.example.carrentalapp.model.CarDto
import com.example.carrentalapp.model.CarStatusUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminUiState(
    val cars: List<CarDto> = emptyList(),
    val isLoading: Boolean = false,
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

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null, error = null)
    }
}

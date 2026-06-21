package com.example.carrentalapp.statemanagement

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carrentalapp.apiclient.ApiClient
import com.example.carrentalapp.localcache.AppDatabase
import com.example.carrentalapp.localcache.CarCacheEntity
import com.example.carrentalapp.model.CarDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SortOrder { DEFAULT, PRICE_ASC, PRICE_DESC }

data class CatalogUiState(
    val cars: List<CarDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFromCache: Boolean = false,
    val searchQuery: String = "",
    val sortOrder: SortOrder = SortOrder.DEFAULT
)

class CatalogViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    private var allCars: List<CarDto> = emptyList()

    fun loadCars(context: Context, carClass: String? = null, startDate: String? = null, endDate: String? = null) {
        val isFiltered = startDate != null && endDate != null
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = ApiClient.carApi.getCars(carClass, startDate, endDate)
                if (response.isSuccessful && response.body() != null) {
                    val cars = response.body()!!
                    if (!isFiltered) updateCache(context, cars)
                    allCars = cars
                    _uiState.value = _uiState.value.copy(isLoading = false, isFromCache = false, error = null)
                    applyFilters()
                } else if (isFiltered) {
                    allCars = emptyList()
                    val errorMsg = when (response.code()) {
                        401 -> "Сессия истекла, войдите заново"
                        403 -> "Недостаточно прав для просмотра каталога"
                        500 -> "Ошибка сервера, попробуйте позже"
                        else -> "Не удалось загрузить каталог (код ${response.code()})"
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false, isFromCache = false, error = errorMsg)
                    applyFilters()
                } else {
                    val errorMsg = "Не удалось загрузить каталог (код ${response.code()})"
                    _uiState.value = _uiState.value.copy(isLoading = false, isFromCache = false, error = errorMsg)
                    loadFromCache(context)
                }
            } catch (e: Exception) {
                val errorMsg = when {
                    e.message?.contains("Unable to resolve host") == true ||
                    e.message?.contains("Connection refused") == true ->
                        "Сервер недоступен. Убедитесь, что бэкенд запущен"
                    e.message?.contains("timeout") == true ||
                    e.message?.contains("timed out") == true ->
                        "Сервер не отвечает (таймаут)"
                    else -> null // Показываем кэш без сообщения об ошибке
                }
                if (isFiltered) {
                    allCars = emptyList()
                    _uiState.value = _uiState.value.copy(isLoading = false, isFromCache = false,
                        error = errorMsg ?: "Нет соединения с сервером")
                    applyFilters()
                } else {
                    loadFromCache(context, errorMsg)
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun setSortOrder(order: SortOrder) {
        _uiState.value = _uiState.value.copy(sortOrder = order)
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        val query = state.searchQuery.trim()
        var result = allCars
        if (query.isNotEmpty()) {
            result = result.filter { it.modelName.contains(query, ignoreCase = true) }
        }
        result = when (state.sortOrder) {
            SortOrder.PRICE_ASC -> result.sortedBy { it.baseDailyRate }
            SortOrder.PRICE_DESC -> result.sortedByDescending { it.baseDailyRate }
            SortOrder.DEFAULT -> result
        }
        _uiState.value = _uiState.value.copy(cars = result)
    }

    private suspend fun loadFromCache(context: Context, apiError: String? = null) {
        val db = AppDatabase.getInstance(context)
        val cached = db.carDao().getAllCars()
        allCars = cached.map { it.toDto() }
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isFromCache = true,
            error = if (allCars.isEmpty()) (apiError ?: "Нет соединения, а кэш пуст") else apiError
        )
        applyFilters()
    }

    private suspend fun updateCache(context: Context, cars: List<CarDto>) {
        val db = AppDatabase.getInstance(context)
        db.carDao().clearAll()
        db.carDao().insertAll(cars.map { it.toCacheEntity() })
    }

    private fun CarCacheEntity.toDto() = CarDto(id, modelName, carClass, baseDailyRate, status, imageUrl, licensePlate, vin)
    private fun CarDto.toCacheEntity() = CarCacheEntity(id, modelName, carClass, baseDailyRate, status, imageUrl, licensePlate, vin)
}

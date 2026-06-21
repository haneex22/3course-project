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
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = ApiClient.carApi.getCars(carClass, startDate, endDate)
                if (response.isSuccessful && response.body() != null) {
                    val cars = response.body()!!
                    // Кэшируем только полный список (без фильтра по датам), чтобы офлайн был полным
                    if (!isFiltered) updateCache(context, cars)
                    allCars = cars
                    _uiState.value = _uiState.value.copy(isLoading = false, isFromCache = false, error = null)
                    applyFilters()
                } else if (isFiltered) {
                    // При фильтре по датам НЕ показываем весь кэш — иначе всплывут занятые авто
                    allCars = emptyList()
                    _uiState.value = _uiState.value.copy(isLoading = false, isFromCache = false,
                        error = "Не удалось проверить доступность на эти даты")
                    applyFilters()
                } else {
                    loadFromCache(context)
                }
            } catch (e: Exception) {
                if (isFiltered) {
                    allCars = emptyList()
                    _uiState.value = _uiState.value.copy(isLoading = false, isFromCache = false,
                        error = "Нет соединения с сервером")
                    applyFilters()
                } else {
                    loadFromCache(context)
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

    private suspend fun loadFromCache(context: Context) {
        val db = AppDatabase.getInstance(context)
        val cached = db.carDao().getAllCars()
        allCars = cached.map { it.toDto() }
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isFromCache = true,
            error = if (allCars.isEmpty()) "Нет соединения, а кэш пуст" else null
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

package com.example.carrentalapp.statemanagement

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carrentalapp.apiclient.ApiClient
import com.example.carrentalapp.localcache.TokenStorage
import com.example.carrentalapp.model.LoginRequest
import com.example.carrentalapp.model.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun login(context: Context, email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val response = ApiClient.authApi.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    TokenStorage.save(context, body.token, body.userId, body.email, body.role)
                    _state.value = AuthState.Success
                } else {
                    _state.value = AuthState.Error("Invalid credentials")
                }
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Network error")
            }
        }
    }

    fun register(context: Context, email: String, password: String, confirm: String) {
        if (password != confirm) {
            _state.value = AuthState.Error("Пароли не совпадают")
            return
        }
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val response = ApiClient.authApi.register(RegisterRequest(email, password, confirm))
                if (response.isSuccessful) {
                    _state.value = AuthState.Success
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = when (response.code()) {
                        409 -> "Пользователь с таким email уже существует"
                        400 -> "Проверьте правильность введённых данных"
                        else -> "Ошибка регистрации (${response.code()})"
                    }
                    _state.value = AuthState.Error(message)
                }
            } catch (e: Exception) {
                _state.value = AuthState.Error("Нет соединения с сервером")
            }
        }
    }

    fun resetState() {
        _state.value = AuthState.Idle
    }
}

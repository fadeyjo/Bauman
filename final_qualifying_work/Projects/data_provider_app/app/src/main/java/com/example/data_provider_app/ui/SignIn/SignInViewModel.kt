package com.example.data_provider_app.ui.SignIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data_provider_app.retrofit_client.RetrofitClient
import com.example.data_provider_app.util.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

    private val _state = MutableStateFlow<SignInState>(SignInState.Idle)
    val state: StateFlow<SignInState> = _state

    fun login(email: String, password: String) {
        viewModelScope.launch {

            _state.value = SignInState.Loading

            val result = RetrofitClient
                .personsRepository
                .login(email, password)

            _state.value = when (result) {

                is ApiResult.Success -> {
                    SignInState.Success(result.data!!.accessToken, result.data.refreshToken)
                }

                is ApiResult.Error -> {
                    when (result.code) {
                        401 -> SignInState.PasswordError("Неверный пароль")
                        404 -> SignInState.EmailError("Пользователь не найден")
                        else -> SignInState.GeneralError("Ошибка сервера")
                    }
                }

                is ApiResult.NetworkError ->
                    SignInState.GeneralError("Нет подключения к интернету")

                is ApiResult.UnknownError ->
                    SignInState.GeneralError("Неизвестная ошибка")

                is ApiResult.ValidationError -> {
                    val mapErrors = result.errors.mapNotNull { (key, list) -> list.firstOrNull()?.let { first -> key to first } }.toMap()
                    SignInState.ValidationError(mapErrors)
                }
            }
        }
    }

    fun resetState() {
        _state.value = SignInState.Idle
    }
}

sealed class SignInState {
    object Idle : SignInState()
    object Loading : SignInState()
    data class Success(val accessToken: String, val refreshToken: String) : SignInState()
    data class EmailError(val message: String) : SignInState()
    data class PasswordError(val message: String) : SignInState()
    data class GeneralError(val message: String) : SignInState()
    data class ValidationError(val map: Map<String, String>) : SignInState()
}

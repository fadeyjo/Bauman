package com.example.data_provider_app.ui.Splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data_provider_app.retrofit_client.RetrofitClient
import com.example.data_provider_app.util.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state: StateFlow<SplashState> = _state

    fun checkToken(token: String) {
        viewModelScope.launch {
            val result = RetrofitClient.refreshTokenRepository.checkToken(token)

            _state.value = when (result) {
                is ApiResult.NetworkError -> SplashState.NetworkError
                is ApiResult.Success -> SplashState.Authorized
                is ApiResult.UnknownError -> SplashState.UnknownError
                is ApiResult.Error -> {
                    when (result.code) {
                        401 -> SplashState.NotAuthorized
                        else -> SplashState.ServerError
                    }
                }
                is ApiResult.ValidationError -> SplashState.ValidationError
            }
        }
    }
}

sealed class SplashState {
    object Loading : SplashState()
    object Authorized : SplashState()
    object NotAuthorized : SplashState()
    object NetworkError : SplashState()
    object ServerError : SplashState()
    object UnknownError : SplashState()
    object ValidationError : SplashState()
}
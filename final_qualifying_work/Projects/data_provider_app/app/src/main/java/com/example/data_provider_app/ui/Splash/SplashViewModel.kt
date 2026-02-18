package com.example.data_provider_app.ui.Splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data_provider_app.model.dto.CheckPasswordRequest
import com.example.data_provider_app.util.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state: StateFlow<SplashState> = _state

    fun checkUser(email: String, password: String) {
        viewModelScope.launch {
            val request = CheckPasswordRequest(email, password)
            val result = PersonRepository.checkPassword(request)

            _state.value = when (result) {
                is ApiResult.Success -> {
                    if (result.data.confirmed) {
                        SplashState.Authorized
                    }
                    else {
                        SplashState.NotAuthorized
                    }
                }
                else -> SplashState.Error
            }
        }
    }
}

sealed class SplashState {
    object Loading : SplashState()
    object Authorized : SplashState()
    object NotAuthorized : SplashState()
    object Error : SplashState()
}

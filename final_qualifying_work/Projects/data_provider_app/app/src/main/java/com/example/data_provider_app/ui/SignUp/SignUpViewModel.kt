package com.example.data_provider_app.ui.SignUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data_provider_app.dto.CreatePersonDto
import com.example.data_provider_app.retrofit_client.RetrofitClient
import com.example.data_provider_app.ui.SignIn.SignInState
import com.example.data_provider_app.util.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class SignUpViewModel : ViewModel() {
    private val _state = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val state: StateFlow<SignUpState> = _state

    fun signUp(
        email: String, phone: String,
        lastName: String, firstName: String,
        patronymic: String?, birth: LocalDate,
        password: String, driveLicense: String
    ) {
        viewModelScope.launch {
            _state.value = SignUpState.Loading

            val result = RetrofitClient
                .personsRepository
                .createPerson(
                    email, phone,
                    lastName, firstName,
                    patronymic, birth,
                    password, driveLicense,
                    0u
                )

            _state.value = when(result) {
                is ApiResult.Error -> {
                    if (result.code == 409) {
                        if (result.error == "Пользователь с данным email уже существует")
                        {
                            SignUpState.EmailExists
                        }
                        else if (result.error == "Пользователь с данным номером телефона уже существует") {
                            SignUpState.PhoneExists
                        }
                        else {
                            SignUpState.DriveLicenseExists
                        }
                    }
                    else if (result.code == 500) {
                        SignUpState.ServerError
                    }
                    else {
                        SignUpState.UnknownError
                    }
                }
                is ApiResult.NetworkError -> SignUpState.NetworkError
                is ApiResult.Success -> SignUpState.Registered
                is ApiResult.UnknownError -> SignUpState.UnknownError
                is ApiResult.ValidationError -> {
                    val mapErrors = result.errors.mapNotNull { (key, list) -> list.firstOrNull()?.let { first -> key to first } }.toMap()
                    SignUpState.ValidationError(mapErrors)
                }
            }
        }
    }

    fun resetState() {
        _state.value = SignUpState.Idle
    }
}

sealed class SignUpState {
    object Idle: SignUpState()
    object Registered: SignUpState()
    object PhoneExists: SignUpState()
    object EmailExists: SignUpState()
    object DriveLicenseExists: SignUpState()
    data class ValidationError(val map: Map<String, String>) : SignUpState()
    object UnknownError: SignUpState()
    object NetworkError: SignUpState()
    object ServerError: SignUpState()
    object  Loading: SignUpState()
}

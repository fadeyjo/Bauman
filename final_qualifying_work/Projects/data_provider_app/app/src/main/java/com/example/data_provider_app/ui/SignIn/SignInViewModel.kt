package com.example.data_provider_app.ui.SignIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data_provider_app.model.dto.CheckPasswordRequest
import com.example.data_provider_app.model.entity.Person
import com.example.data_provider_app.util.ApiResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

    private val _state = MutableStateFlow(SignInUiState())
    val state: StateFlow<SignInUiState> = _state

    private val _events = MutableSharedFlow<SignInEvent>()
    val events = _events.asSharedFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {

            _state.update { it.copy(isLoading = true) }

            val personResponse = PersonRepository.getPersonByEmail(email)

            when (personResponse) {
                is ApiResult.BadRequest -> {
                    _state.update { it.copy(isLoading = false) }
                    _events.emit(SignInEvent.ShowError(personResponse.error))
                    return@launch
                }
                is ApiResult.InternalServerError -> {
                    _state.update { it.copy(isLoading = false) }
                    _events.emit(SignInEvent.ShowError(personResponse.error))
                    return@launch
                }
                is ApiResult.NetworkError -> {
                    _state.update { it.copy(isLoading = false) }
                    _events.emit(SignInEvent.ShowError("Отсутствует соединение с сервером"))
                    return@launch
                }
                is ApiResult.NotFound -> {
                    _state.update { it.copy(isLoading = false) }
                    _events.emit(SignInEvent.UserNotFound)
                    return@launch
                }
                is ApiResult.Success -> {  }
                else -> {
                    _state.update { it.copy(isLoading = false) }
                    _events.emit(SignInEvent.ShowError("Неизвестная ошибка"))
                    return@launch
                }
            }

            val passwordResponse =
                PersonRepository.checkPassword(
                    CheckPasswordRequest(email, password)
                )

            _state.update { it.copy(isLoading = false) }

            when (passwordResponse) {
                is ApiResult.BadRequest -> _events.emit(SignInEvent.ShowError(passwordResponse.error))
                is ApiResult.InternalServerError -> _events.emit(SignInEvent.ShowError(passwordResponse.error))
                is ApiResult.NetworkError -> _events.emit(SignInEvent.ShowError("Отсутствует соединение с сервером"))
                is ApiResult.Success -> {
                    if (passwordResponse.data.confirmed)
                        _events.emit(SignInEvent.NavigateToMain(personResponse.data))
                    else
                        _events.emit(SignInEvent.WrongPassword)
                }
                else -> _events.emit(SignInEvent.ShowError("Неизвестная ошибка"))
            }
        }
    }
}


data class SignInUiState(
    val isLoading: Boolean = false
)

sealed class SignInEvent {
    object UserNotFound : SignInEvent()
    object WrongPassword : SignInEvent()
    data class NavigateToMain(val person: Person) : SignInEvent()
    data class ShowError(val message: String?) : SignInEvent()
}

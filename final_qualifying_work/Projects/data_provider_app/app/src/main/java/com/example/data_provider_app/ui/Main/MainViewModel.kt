package com.example.data_provider_app.ui.Main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data_provider_app.dto.CarDto
import com.example.data_provider_app.dto.PersonDto
import com.example.data_provider_app.retrofit_client.RetrofitClient
import com.example.data_provider_app.util.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate

class MainViewModel : ViewModel() {
    private val _userState = MutableStateFlow<UserViewState>(UserViewState.Idle)
    val userState: StateFlow<UserViewState> = _userState

    private val _logoutState = MutableStateFlow<LogoutViewState>(LogoutViewState.Idle)
    val logoutState: StateFlow<LogoutViewState> = _logoutState

    private val _updateProfileInfoState = MutableStateFlow<UpdateProfileInfoState>(UpdateProfileInfoState.Idle)
    val updateProfileInfoState: StateFlow<UpdateProfileInfoState> = _updateProfileInfoState

    private val _myCarsState = MutableStateFlow<MyCarsState>(MyCarsState.Idle)
    val myCarsState: StateFlow<MyCarsState> = _myCarsState

    var person: PersonDto? = null

    fun getUserInfo() {
        viewModelScope.launch {
            _userState.value = UserViewState.Loading

            val userData =
                RetrofitClient
                    .personsRepository
                    .getPersonByEmail()

            _userState.value = when (userData) {
                is ApiResult.Error -> UserViewState.Error(userData.error)
                is ApiResult.NetworkError -> UserViewState.NetworkError
                is ApiResult.Success -> {
                    person = userData.data

                    UserViewState.Data(
                        userData.data!!
                    )
                }
                is ApiResult.UnknownError -> UserViewState.UnknownError
                is ApiResult.ValidationError -> UserViewState.ValidationError
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _logoutState.value = LogoutViewState.Loading

            val result =
                RetrofitClient
                    .personsRepository
                    .logout()

            _logoutState.value = when (result) {
                is ApiResult.Error -> LogoutViewState.Error(result.error)
                is ApiResult.NetworkError -> LogoutViewState.NetworkError
                is ApiResult.Success<*> -> LogoutViewState.Logout
                is ApiResult.UnknownError -> LogoutViewState.UnknownError
                is ApiResult.ValidationError -> LogoutViewState.ValidationError
            }
        }
    }

    fun updateProfileInfo(
        email: String,
        phone: String, lastName: String,
        firstName: String, patronymic: String?,
        birth: LocalDate, driveLicense: String?,
        file: File?, mimeType: String
    ) {
        viewModelScope.launch {
            _updateProfileInfoState.value = UpdateProfileInfoState.Loading

            val result = RetrofitClient.personsRepository.updatePersonInfo(
                email, phone,
                lastName, firstName,
                patronymic, birth,
                driveLicense
            )

            var isOk = false

            when (result) {
                is ApiResult.Error -> _updateProfileInfoState.value = UpdateProfileInfoState.Error(result.error)
                is ApiResult.NetworkError -> _updateProfileInfoState.value = UpdateProfileInfoState.NetworkError
                is ApiResult.Success<*> -> isOk = true
                is ApiResult.UnknownError -> _updateProfileInfoState.value = UpdateProfileInfoState.UnknownError
                is ApiResult.ValidationError -> {
                    val mapErrors = result.errors.mapNotNull { (key, list) -> list.firstOrNull()?.let { first -> key to first } }.toMap()
                    _updateProfileInfoState.value = UpdateProfileInfoState.ValidationError(mapErrors)
                }
            }

            if (!isOk) return@launch

            if (file == null) {
                _updateProfileInfoState.value = UpdateProfileInfoState.Updated
                return@launch
            }

            val avatarResult = RetrofitClient.avatarRepository.newAvatar(file, mimeType)

            _updateProfileInfoState.value = when (avatarResult) {
                is ApiResult.Error -> UpdateProfileInfoState.Error(avatarResult.error)
                is ApiResult.NetworkError -> UpdateProfileInfoState.NetworkError
                is ApiResult.Success<*> -> UpdateProfileInfoState.Updated
                is ApiResult.UnknownError -> UpdateProfileInfoState.UnknownError
                is ApiResult.ValidationError -> {
                    val mapErrors = avatarResult.errors.mapNotNull { (key, list) -> list.firstOrNull()?.let { first -> key to first } }.toMap()
                    UpdateProfileInfoState.ValidationError(mapErrors)
                }
            }
        }
    }

    fun getMyCars() {
        viewModelScope.launch {
            _myCarsState.value = MyCarsState.Loading

            val result = RetrofitClient.carsRepository.getCarsByPersonId()

            _myCarsState.value = when (result) {
                is ApiResult.Error -> MyCarsState.Error(result.error)
                is ApiResult.NetworkError -> MyCarsState.NetworkError
                is ApiResult.Success -> MyCarsState.Data(result.data ?: emptyList())
                is ApiResult.UnknownError -> MyCarsState.UnknownError
                is ApiResult.ValidationError -> {
                    val mapErrors = result.errors.mapNotNull { (key, list) -> list.firstOrNull()?.let { first -> key to first } }.toMap()
                    MyCarsState.ValidationError(mapErrors)
                }
            }
        }
    }

    fun resetUpdateProfileInfoState() {
        _updateProfileInfoState.value = UpdateProfileInfoState.Idle
    }
}

sealed class UserViewState {
    object Loading : UserViewState()
    object Idle : UserViewState()
    data class Data(
        val person: PersonDto
    ) : UserViewState()
    data class Error(val message: String) : UserViewState()
    object NetworkError : UserViewState()
    object UnknownError : UserViewState()
    object ValidationError : UserViewState()
}

sealed class LogoutViewState {
    object Loading : LogoutViewState()
    object Idle : LogoutViewState()
    object Logout : LogoutViewState()
    data class Error(val message: String) : LogoutViewState()
    object NetworkError : LogoutViewState()
    object UnknownError : LogoutViewState()
    object ValidationError : LogoutViewState()
}

sealed class UpdateProfileInfoState {
    object Loading : UpdateProfileInfoState()
    object Idle : UpdateProfileInfoState()
    object Updated : UpdateProfileInfoState()
    data class Error(val message: String) : UpdateProfileInfoState()
    object NetworkError : UpdateProfileInfoState()
    object UnknownError : UpdateProfileInfoState()
    data class ValidationError(val map: Map<String, String>) : UpdateProfileInfoState()
}

sealed class MyCarsState {
    object Loading : MyCarsState()
    object Idle : MyCarsState()
    data class Data(val cars: List<CarDto>): MyCarsState()
    data class Error(val message: String) : MyCarsState()
    object NetworkError : MyCarsState()
    object UnknownError : MyCarsState()
    data class ValidationError(val map: Map<String, String>) : MyCarsState()
}

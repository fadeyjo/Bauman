package com.example.data_provider_app.ui.Main

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data_provider_app.dto.AvatarDto
import com.example.data_provider_app.dto.PersonDto
import com.example.data_provider_app.dto.UpdatePersonInfoDto
import com.example.data_provider_app.retrofit_client.RetrofitClient
import com.example.data_provider_app.ui.SignUp.SignUpState
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

    var person: PersonDto? = null
    var personBitmap: Bitmap? = null

    fun getUserInfo() {
        viewModelScope.launch {
            _userState.value = UserViewState.Loading

            val avatar =
                RetrofitClient
                    .avatarRepository
                    .getLastAvatar()

            var bitmap: Bitmap? = null

            when (avatar) {
                is ApiResult.Error -> _userState.value = UserViewState.Error(avatar.error)
                is ApiResult.NetworkError -> _userState.value = UserViewState.NetworkError
                is ApiResult.Success -> bitmap = avatar.data
                is ApiResult.UnknownError -> _userState.value = UserViewState.UnknownError
                is ApiResult.ValidationError -> _userState.value = UserViewState.ValidationError
            }

            if (bitmap == null) return@launch

            val userData =
                RetrofitClient
                    .personsRepository
                    .getPersonByEmail()

            _userState.value = when (userData) {
                is ApiResult.Error -> UserViewState.Error(userData.error)
                is ApiResult.NetworkError -> UserViewState.NetworkError
                is ApiResult.Success -> {
                    person = userData.data
                    personBitmap = bitmap

                    UserViewState.Data(
                        userData.data!!,
                        bitmap
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

    fun resetUserState() {
        _userState.value = UserViewState.Idle
    }

    fun resetLogoutState() {
        _logoutState.value = LogoutViewState.Idle
    }

    fun resetUpdateProfileInfoState() {
        _updateProfileInfoState.value = UpdateProfileInfoState.Idle
    }
}

sealed class UserViewState {
    object Loading : UserViewState()
    object Idle : UserViewState()
    data class Data(
        val person: PersonDto,
        val bitmap: Bitmap
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

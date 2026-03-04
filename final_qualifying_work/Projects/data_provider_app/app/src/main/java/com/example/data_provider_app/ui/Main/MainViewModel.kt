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

    private val _fuelTypesState = MutableStateFlow<DirectoryState>(DirectoryState.Idle)
    val fuelTypesState: StateFlow<DirectoryState> = _fuelTypesState

    private val _brandsState = MutableStateFlow<DirectoryState>(DirectoryState.Idle)
    val brandsState: StateFlow<DirectoryState> = _brandsState

    private val _modelsState = MutableStateFlow<DirectoryState>(DirectoryState.Idle)
    val modelsState: StateFlow<DirectoryState> = _modelsState

    private val _bodiesState = MutableStateFlow<DirectoryState>(DirectoryState.Idle)
    val bodiesState: StateFlow<DirectoryState> = _bodiesState

    private val _gearboxesState = MutableStateFlow<DirectoryState>(DirectoryState.Idle)
    val gearboxesState: StateFlow<DirectoryState> = _gearboxesState

    private val _drivesState = MutableStateFlow<DirectoryState>(DirectoryState.Idle)
    val drivesState: StateFlow<DirectoryState> = _drivesState

    private val _addCarState = MutableStateFlow<AddCarState>(AddCarState.Idle)
    val addCarState: StateFlow<AddCarState> = _addCarState

    private val _getCarState = MutableStateFlow<GetCarState>(GetCarState.Idle)
    val getCarState: StateFlow<GetCarState> = _getCarState

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

    fun getAllFuelTypes() {
        viewModelScope.launch {
            _fuelTypesState.value = DirectoryState.Loading

            val result = RetrofitClient.fuelTypesRepository.getAllFuelTypes()

            _fuelTypesState.value = when (result) {
                is ApiResult.Error -> DirectoryState.Error(result.error)
                is ApiResult.NetworkError -> DirectoryState.NetworkError
                is ApiResult.Success -> {
                    if (result.data.isNullOrEmpty()) {
                        DirectoryState.UnknownError
                    }
                    else {
                        DirectoryState.Data(result.data.map { it.typeName })
                    }
                }
                is ApiResult.UnknownError -> DirectoryState.UnknownError
                is ApiResult.ValidationError -> {
                    val mapErrors = result.errors.mapNotNull { (key, list) -> list.firstOrNull()?.let { first -> key to first } }.toMap()
                    DirectoryState.ValidationError(mapErrors)
                }
            }
        }
    }

    fun getAllBrands() {
        viewModelScope.launch {
            _brandsState.value = DirectoryState.Loading

            val result = RetrofitClient.carBrandsRepository.getAllBrands()

            _brandsState.value = when (result) {
                is ApiResult.Error -> DirectoryState.Error(result.error)
                is ApiResult.NetworkError -> DirectoryState.NetworkError
                is ApiResult.Success -> {
                    if (result.data.isNullOrEmpty()) {
                        DirectoryState.UnknownError
                    }
                    else {
                        DirectoryState.Data(result.data)
                    }
                }
                is ApiResult.UnknownError -> DirectoryState.UnknownError
                is ApiResult.ValidationError -> {
                    val mapErrors = result.errors.mapNotNull { (key, list) -> list.firstOrNull()?.let { first -> key to first } }.toMap()
                    DirectoryState.ValidationError(mapErrors)
                }
            }
        }
    }

    fun getAllModels(brandName: String) {
        viewModelScope.launch {
            _modelsState.value = DirectoryState.Loading

            val result = RetrofitClient.carBrandsModelsRepository.getAllModelsByBrand(brandName)

            _modelsState.value = when (result) {
                is ApiResult.Error -> DirectoryState.Error(result.error)
                is ApiResult.NetworkError -> DirectoryState.NetworkError
                is ApiResult.Success -> {
                    if (result.data.isNullOrEmpty()) {
                        DirectoryState.UnknownError
                    }
                    else {
                        DirectoryState.Data(result.data)
                    }
                }
                is ApiResult.UnknownError -> DirectoryState.UnknownError
                is ApiResult.ValidationError -> {
                    val mapErrors = result.errors.mapNotNull { (key, list) -> list.firstOrNull()?.let { first -> key to first } }.toMap()
                    DirectoryState.ValidationError(mapErrors)
                }
            }
        }
    }

    fun getAllBodyTypes() {
        viewModelScope.launch {
            _bodiesState.value = DirectoryState.Loading

            val result = RetrofitClient.carBodiesRepository.getAllCarBodies()

            _bodiesState.value = when (result) {
                is ApiResult.Error -> DirectoryState.Error(result.error)
                is ApiResult.NetworkError -> DirectoryState.NetworkError
                is ApiResult.Success -> {
                    if (result.data.isNullOrEmpty()) {
                        DirectoryState.UnknownError
                    }
                    else {
                        DirectoryState.Data(result.data.map { it.bodyName })
                    }
                }
                is ApiResult.UnknownError -> DirectoryState.UnknownError
                is ApiResult.ValidationError -> {
                    val mapErrors = result.errors.mapNotNull { (key, list) -> list.firstOrNull()?.let { first -> key to first } }.toMap()
                    DirectoryState.ValidationError(mapErrors)
                }
            }
        }
    }

    fun getAllGearboxTypes() {
        viewModelScope.launch {
            _gearboxesState.value = DirectoryState.Loading

            val result = RetrofitClient.carGearboxesRepository.getAllCarGearboxes()

            _gearboxesState.value = when (result) {
                is ApiResult.Error -> DirectoryState.Error(result.error)
                is ApiResult.NetworkError -> DirectoryState.NetworkError
                is ApiResult.Success -> {
                    if (result.data.isNullOrEmpty()) {
                        DirectoryState.UnknownError
                    }
                    else {
                        DirectoryState.Data(result.data.map { it.gearboxName })
                    }
                }
                is ApiResult.UnknownError -> DirectoryState.UnknownError
                is ApiResult.ValidationError -> {
                    val mapErrors = result.errors.mapNotNull { (key, list) -> list.firstOrNull()?.let { first -> key to first } }.toMap()
                    DirectoryState.ValidationError(mapErrors)
                }
            }
        }
    }

    fun getAllDriveTypes() {
        viewModelScope.launch {
            _drivesState.value = DirectoryState.Loading

            val result = RetrofitClient.carDrivesRepository.getAllCarDrives()

            _drivesState.value = when (result) {
                is ApiResult.Error -> DirectoryState.Error(result.error)
                is ApiResult.NetworkError -> DirectoryState.NetworkError
                is ApiResult.Success -> {
                    if (result.data.isNullOrEmpty()) {
                        DirectoryState.UnknownError
                    }
                    else {
                        DirectoryState.Data(result.data.map { it.driveName })
                    }
                }
                is ApiResult.UnknownError -> DirectoryState.UnknownError
                is ApiResult.ValidationError -> {
                    val mapErrors = result.errors.mapNotNull { (key, list) -> list.firstOrNull()?.let { first -> key to first } }.toMap()
                    DirectoryState.ValidationError(mapErrors)
                }
            }
        }
    }

    fun resetUpdateProfileInfoState() {
        _updateProfileInfoState.value = UpdateProfileInfoState.Idle
    }

    fun resetMyCarsState() {
        _myCarsState.value = MyCarsState.Idle
    }

    fun addCar(
        vinNumber: String,
        stateNumber: String?, brandName: String,
        modelName: String, bodyName: String,
        releaseYear: UShort, gearboxName: String,
        driveName: String, vehicleWeightKG: UShort,
        enginePowerHP: UShort, enginePowerKW: Float,
        engineCapacityL: Float, tankCapacityL: UByte,
        fuelTypeName: String
    ) {
        viewModelScope.launch {
            _addCarState.value = AddCarState.Loading

            val result = RetrofitClient.carsRepository.addCar(
                vinNumber,
                stateNumber, brandName,
                modelName, bodyName,
                releaseYear, gearboxName,
                driveName, vehicleWeightKG,
                enginePowerHP, enginePowerKW,
                engineCapacityL, tankCapacityL,
                fuelTypeName
            )

            _addCarState.value = when (result) {
                is ApiResult.Error -> {
                    if (result.code == 409) {
                        if (result.error == "Автомобиль с данным VIN уже существует")
                        {
                            AddCarState.VINExists
                        }
                        else if (result.error == "Автомобиль с данным государственным номером уже существует") {
                            AddCarState.StateNumberExists
                        }
                        else {
                            AddCarState.Error(result.error)
                        }
                    }
                    else {
                        AddCarState.Error(result.error)
                    }
                }
                is ApiResult.NetworkError -> AddCarState.NetworkError
                is ApiResult.Success -> AddCarState.Added
                is ApiResult.UnknownError -> AddCarState.UnknownError
                is ApiResult.ValidationError -> {
                    val mapErrors = result.errors.mapNotNull { (key, list) -> list.firstOrNull()?.let { first -> key to first } }.toMap()
                    AddCarState.ValidationError(mapErrors)
                }
            }
        }
    }

    fun getCarData(VIN: String) {
        viewModelScope.launch {
            _getCarState.value = GetCarState.Loading

            val result = RetrofitClient.carsRepository.getCarByVINNumber(VIN)

            _getCarState.value = when (result) {
                is ApiResult.Error -> GetCarState.Error(result.error)
                is ApiResult.NetworkError -> GetCarState.NetworkError
                is ApiResult.Success -> {
                    if (result.data == null) {
                        GetCarState.UnknownError
                    }
                    else {
                        GetCarState.Data(result.data)
                    }
                }
                is ApiResult.UnknownError -> GetCarState.UnknownError
                is ApiResult.ValidationError -> {
                    val mapErrors = result.errors.mapNotNull { (key, list) -> list.firstOrNull()?.let { first -> key to first } }.toMap()
                    GetCarState.ValidationError(mapErrors)
                }
            }
        }
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

sealed class DirectoryState {
    object Loading : DirectoryState()
    object Idle : DirectoryState()
    data class Data(val items: List<String>): DirectoryState()
    data class Error(val message: String) : DirectoryState()
    object NetworkError : DirectoryState()
    object UnknownError : DirectoryState()
    data class ValidationError(val map: Map<String, String>) : DirectoryState()
}

sealed class AddCarState {
    object Loading : AddCarState()
    object Idle : AddCarState()
    object Added: AddCarState()
    data class Error(val message: String) : AddCarState()
    object NetworkError : AddCarState()
    object UnknownError : AddCarState()
    data class ValidationError(val map: Map<String, String>) : AddCarState()
    object VINExists: AddCarState()
    object StateNumberExists: AddCarState()
}

sealed class GetCarState {
    object Loading : GetCarState()
    object Idle : GetCarState()
    data class Data(val car: CarDto): GetCarState()
    data class Error(val message: String) : GetCarState()
    object NetworkError : GetCarState()
    object UnknownError : GetCarState()
    data class ValidationError(val map: Map<String, String>) : GetCarState()
}

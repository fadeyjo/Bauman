package com.example.data_provider_app.repository

import com.example.data_provider_app.api.CarApi
import com.example.data_provider_app.dto.CarDto
import com.example.data_provider_app.dto.CreateCarDto
import com.example.data_provider_app.dto.UpdateCarInfoDto
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult

class CarsRepository(
    private val api: CarApi
) {
    suspend fun getCarByVINNumber(VINNumber: String): ApiResult<List<CarDto>> {
        return ApiResponseHandler.handleResponse(api.getCarByVINNumber(VINNumber))
    }

    suspend fun addCar(
        VINNumber: String,
        stateNumber: String?, brandName: String,
        modelName: String, bodyName: String,
        releaseYear: UShort, gearboxName: String,
        driveName: String, vehicleWeightKG: UShort,
        enginePowerHP: UShort, enginePowerKW: Float,
        engineCapacityL: Float, engineTypeName: String,
        tankCapacityL: UByte, fuelTypeName: String
    ): ApiResult<CarDto> {
        val car = CreateCarDto(
            VINNumber,
            stateNumber, brandName,
            modelName, bodyName,
            releaseYear, gearboxName,
            driveName, vehicleWeightKG,
            enginePowerHP, enginePowerKW,
            engineCapacityL, engineTypeName,
            tankCapacityL, fuelTypeName
        )

        return try {
            ApiResponseHandler.handleResponse(api.addCar(car))
        }
        catch (e: Exception) {
            ApiResult.NetworkError
        }
    }

    suspend fun getCarsByPersonId(): ApiResult<List<CarDto>> {
        return try {
            ApiResponseHandler.handleResponse(api.getCarsByPersonId())
        }
        catch (e: Exception) {
            ApiResult.NetworkError
        }
    }

    suspend fun updateCarInfo(
        carId: UInt, VINNumber: String,
        stateNumber: String?, brandName: String,
        modelName: String, bodyName: String,
        releaseYear: UShort, gearboxName: String,
        driveName: String, vehicleWeightKG: UShort,
        enginePowerHP: UShort, enginePowerKW: Float,
        engineCapacityL: Float, engineTypeName: String,
        tankCapacityL: UByte, fuelTypeName: String
    ): ApiResult<Unit> {
        val car = UpdateCarInfoDto(
            carId, VINNumber,
            stateNumber, brandName,
            modelName, bodyName,
            releaseYear, gearboxName,
            driveName, vehicleWeightKG,
            enginePowerHP, enginePowerKW,
            engineCapacityL, engineTypeName,
            tankCapacityL, fuelTypeName
        )

        return try {
            ApiResponseHandler.handleResponse(api.updateCarInfo(car))
        }
        catch (e: Exception) {
            ApiResult.NetworkError
        }
    }
}

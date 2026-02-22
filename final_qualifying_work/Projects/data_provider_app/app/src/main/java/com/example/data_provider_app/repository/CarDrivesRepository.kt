package com.example.data_provider_app.repository

import com.example.data_provider_app.api.CarDriveApi
import com.example.data_provider_app.dto.CarDriveDto
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult

class CarDrivesRepository(
    private val api: CarDriveApi
) {
    suspend fun getAllCarDrives(): ApiResult<List<CarDriveDto>> {
        return try {
            ApiResponseHandler.handleResponse(api.getAllCarDrives())
        }
        catch (e: Exception) {
            ApiResult.NetworkError
        }
    }
}

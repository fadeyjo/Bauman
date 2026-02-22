package com.example.data_provider_app.repository

import com.example.data_provider_app.api.FuelTypeApi
import com.example.data_provider_app.dto.FuelTypeDto
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult

class FuelTypesRepository(
    private val api: FuelTypeApi
) {
    suspend fun getAllEngineTypes(): ApiResult<List<FuelTypeDto>> {
        return try {
            ApiResponseHandler.handleResponse(api.getAllFuelTypes())
        }
        catch (e: Exception) {
            ApiResult.NetworkError
        }
    }
}

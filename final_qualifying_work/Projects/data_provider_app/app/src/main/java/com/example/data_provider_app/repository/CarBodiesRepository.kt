package com.example.data_provider_app.repository

import com.example.data_provider_app.api.CarBodyApi
import com.example.data_provider_app.dto.CarBodyDto
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult

class CarBodiesRepository(
    private val api: CarBodyApi
) {
    suspend fun getAllCarBodies(): ApiResult<List<CarBodyDto>> {
        return try {
            ApiResponseHandler.handleResponse(api.getAllCarBodies())
        }
        catch (e: Exception)
        {
            ApiResult.NetworkError
        }
    }
}

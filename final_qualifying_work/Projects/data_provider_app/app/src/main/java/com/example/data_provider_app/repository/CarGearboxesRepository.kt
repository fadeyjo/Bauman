package com.example.data_provider_app.repository

import com.example.data_provider_app.api.CarGearboxApi
import com.example.data_provider_app.dto.CarGearboxDto
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult

class CarGearboxesRepository(
    private val api: CarGearboxApi
) {
    suspend fun getAllCarGearboxes(): ApiResult<List<CarGearboxDto>> {
        return ApiResponseHandler.handleResponse(api.getAllCarGearboxes())
    }
}

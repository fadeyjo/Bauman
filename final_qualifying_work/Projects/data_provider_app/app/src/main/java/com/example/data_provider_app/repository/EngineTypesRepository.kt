package com.example.data_provider_app.repository

import com.example.data_provider_app.api.EngineTypeApi
import com.example.data_provider_app.dto.EngineTypeDto
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult

class EngineTypesRepository(
    private val api: EngineTypeApi
) {
    suspend fun getAllEngineTypes(): ApiResult<List<EngineTypeDto>> {
        return ApiResponseHandler.handleResponse(api.getAllEngineTypes())
    }
}


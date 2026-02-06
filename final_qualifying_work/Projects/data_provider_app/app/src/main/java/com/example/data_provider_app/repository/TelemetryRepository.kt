package com.example.data_provider_app.repository

import com.example.data_provider_app.model.dto.CreateTelemetryDataRequest
import com.example.data_provider_app.model.dto.RecIdResponse
import com.example.data_provider_app.remote.RetrofitClient
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult

object TelemetryRepository {
    suspend fun createTelemetryData(telemetryData: CreateTelemetryDataRequest): ApiResult<RecIdResponse> {
        val response = RetrofitClient.api.createTelemtryData(telemetryData)
        return ApiResponseHandler.handleResponse(response)
    }
}
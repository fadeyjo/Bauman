package com.example.data_provider_app.repository

import com.example.data_provider_app.model.dto.CreateGPSDataRequest
import com.example.data_provider_app.model.dto.RecIdResponse
import com.example.data_provider_app.remote.RetrofitClient
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult

object GPSDataRepository {
    suspend fun createGPSData(GPSdata: CreateGPSDataRequest): ApiResult<RecIdResponse> {
        val response = RetrofitClient.api.createGPSData(GPSdata)
        return ApiResponseHandler.handleResponse(response)
    }
}
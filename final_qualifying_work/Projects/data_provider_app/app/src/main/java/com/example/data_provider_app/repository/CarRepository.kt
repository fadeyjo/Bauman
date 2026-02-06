package com.example.data_provider_app.repository

import com.example.data_provider_app.model.dto.CarResponse
import com.example.data_provider_app.model.dto.CreateCarRequest
import com.example.data_provider_app.model.dto.CreateCarResponse
import com.example.data_provider_app.remote.RetrofitClient
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult

object CarRepository {
    suspend fun addCar(car: CreateCarRequest): ApiResult<CreateCarResponse> {
        val response = RetrofitClient.api.addCar(car)
        return ApiResponseHandler.handleResponse(response)
    }

    suspend fun getCarsByPersonId(personId: UInt): ApiResult<List<CarResponse>> {
        val response = RetrofitClient.api.getCarsByPersonId(personId)
        return ApiResponseHandler.handleResponse(response)
    }
}
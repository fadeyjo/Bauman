package com.example.data_provider_app.repository

import com.example.data_provider_app.model.dto.EndTripRequest
import com.example.data_provider_app.model.dto.StartTripRequest
import com.example.data_provider_app.model.dto.TripIdResponse
import com.example.data_provider_app.remote.RetrofitClient
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult

object TripRepository {
    suspend fun startTrip(tripData: StartTripRequest): ApiResult<TripIdResponse> {
        val response = RetrofitClient.api.startTrip(tripData)
        return ApiResponseHandler.handleResponse(response)
    }

    suspend fun endTrip(tripData: EndTripRequest, tripId: ULong): ApiResult<TripIdResponse> {
        val response = RetrofitClient.api.endTrip(tripData, tripId)
        return ApiResponseHandler.handleResponse(response)
    }
}
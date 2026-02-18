package com.example.data_provider_app.repository

import com.example.data_provider_app.api.TripApi
import com.example.data_provider_app.dto.StartTripDto
import com.example.data_provider_app.dto.TripDto
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult
import java.time.LocalDateTime

class TripsRepository(
    private val api: TripApi
)
{
    suspend fun startTrip(
        startDatetime: LocalDateTime, MACAddress: String,
        carId: UInt
    ): ApiResult<TripDto> {
        val startTrip = StartTripDto(
            startDatetime, MACAddress,
            carId
        )

        return ApiResponseHandler.handleResponse(api.startTrip(startTrip))
    }

    suspend fun getTripById(tripId: ULong): ApiResult<TripDto> {
        return ApiResponseHandler.handleResponse(api.getTripById(tripId))
    }

    suspend fun endTrip(tripId: ULong): ApiResult<Unit> {
        return ApiResponseHandler.handleResponse(api.endTrip(tripId))
    }
}
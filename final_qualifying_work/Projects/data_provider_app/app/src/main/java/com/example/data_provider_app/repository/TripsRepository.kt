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

        return try {
            ApiResponseHandler.handleResponse(api.startTrip(startTrip))
        }
        catch (e: Exception) {
            ApiResult.NetworkError
        }
    }

    suspend fun getTripById(tripId: ULong): ApiResult<TripDto> {
        return try {
            ApiResponseHandler.handleResponse(api.getTripById(tripId))
        }
        catch (e: Exception) {
            ApiResult.NetworkError
        }
    }

    suspend fun endTrip(tripId: ULong): ApiResult<Unit> {
        return try {
            ApiResponseHandler.handleResponse(api.endTrip(tripId))
        }
        catch (e: Exception) {
            ApiResult.NetworkError
        }
    }
}
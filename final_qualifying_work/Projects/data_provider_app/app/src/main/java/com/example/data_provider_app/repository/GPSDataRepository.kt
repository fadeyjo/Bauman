package com.example.data_provider_app.repository

import com.example.data_provider_app.api.GPSDataApi
import com.example.data_provider_app.dto.CreateGPSDataDto
import com.example.data_provider_app.dto.GPSDataDto
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult
import java.time.LocalDateTime

class GPSDataRepository(
    private val api: GPSDataApi
) {
    suspend fun getGPSDataById(recId: ULong): ApiResult<GPSDataDto> {
        return try {
            ApiResponseHandler.handleResponse(api.getGPSDataById(recId))
        }
        catch (e: Exception) {
            ApiResult.NetworkError
        }
    }

    suspend fun createGPSData(
        recDatetime: LocalDateTime, tripId: ULong,
        latitudeDEG: Float, longitudeDEG: Float,
        accuracyM: Float?, speedKMH: Int?,
        bearingDEG: UShort?
    ): ApiResult<GPSDataDto> {
        val GPSData = CreateGPSDataDto(
            recDatetime, tripId,
            latitudeDEG, longitudeDEG,
            accuracyM, speedKMH,
            bearingDEG
        )

        return try {
            ApiResponseHandler.handleResponse(api.createGPSData(GPSData))
        }
        catch (e: Exception) {
            ApiResult.NetworkError
        }
    }

    suspend fun getGPSDataByTripId(tripId: ULong): ApiResult<List<GPSDataDto>> {
        return try {
            ApiResponseHandler.handleResponse(api.getGPSDataByTripId(tripId))
        }
        catch (e: Exception) {
            ApiResult.NetworkError
        }
    }
}
package com.example.data_provider_app.repository

import com.example.data_provider_app.api.TelemetryDataApi
import com.example.data_provider_app.dto.CreateTelemetryDataDto
import com.example.data_provider_app.dto.TelemetryDataDto
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult
import java.time.LocalDateTime

class TelemetryDataRepository(
    private val api: TelemetryDataApi
) {
    suspend fun getTelemetryDataById(recId: ULong): ApiResult<TelemetryDataDto> {
        return ApiResponseHandler.handleResponse(api.getTelemetryDataById(recId))
    }

    suspend fun createTelemtryData(
        recDatetime: LocalDateTime, serviceId: UByte,
        PID: UShort, ECUId: ByteArray,
        responseDlc: UByte, response: ByteArray?,
        tripId: ULong
    ): ApiResult<TelemetryDataDto> {
        val telemetryData = CreateTelemetryDataDto(
            recDatetime, serviceId,
            PID, ECUId,
            responseDlc, response,
            tripId
        )

        return ApiResponseHandler.handleResponse(api.createTelemtryData(telemetryData))
    }

    suspend fun getTelemetryDataByTripId(tripId: ULong): ApiResult<List<TelemetryDataDto>> {
        return ApiResponseHandler.handleResponse(api.getTelemetryDataByTripId(tripId))
    }
}
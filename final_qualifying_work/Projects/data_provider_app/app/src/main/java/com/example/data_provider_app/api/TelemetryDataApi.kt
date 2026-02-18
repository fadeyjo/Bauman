package com.example.data_provider_app.api

import com.example.data_provider_app.dto.CreateTelemetryDataDto
import com.example.data_provider_app.dto.TelemetryDataDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TelemetryDataApi {
    @GET("telemetrydata/rec_id/{recId}")
    suspend fun getTelemetryDataById(@Path("recId") recId: ULong): Response<TelemetryDataDto>

    @POST("telemetrydata")
    suspend fun createTelemtryData(@Body telemetryData: CreateTelemetryDataDto): Response<TelemetryDataDto>

    @GET("telemetrydata/trip_id/{tripId}")
    suspend fun getTelemetryDataByTripId(@Path("tripId") tripId: ULong): Response<List<TelemetryDataDto>>
}

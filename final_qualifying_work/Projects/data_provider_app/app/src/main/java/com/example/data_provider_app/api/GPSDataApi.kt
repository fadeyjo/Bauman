package com.example.data_provider_app.api

import com.example.data_provider_app.dto.CreateGPSDataDto
import com.example.data_provider_app.dto.GPSDataDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GPSDataApi {
    @GET("gpsdata/rec_id/{recId}")
    suspend fun getGPSDataById(@Path("recId") recId: ULong): Response<GPSDataDto>

    @POST("gpsdata")
    suspend fun createGPSData(@Body GPSData: CreateGPSDataDto): Response<GPSDataDto>

    @GET("gpsdata/trip_id/{tripId}")
    suspend fun getGPSDataByTripId(@Path("tripId") tripId: ULong): Response<List<GPSDataDto>>
}

package com.example.data_provider_app.api

import com.example.data_provider_app.dto.CarDriveDto
import retrofit2.Response
import retrofit2.http.GET

interface CarDriveApi {
    @GET("cardrives")
    suspend fun getAllCarDrives(): Response<List<CarDriveDto>>
}

package com.example.data_provider_app.api

import com.example.data_provider_app.dto.FuelTypeDto
import retrofit2.Response
import retrofit2.http.GET

interface FuelTypeApi {
    @GET("fueltypes")
    suspend fun getAllFuelTypes(): Response<List<FuelTypeDto>>
}

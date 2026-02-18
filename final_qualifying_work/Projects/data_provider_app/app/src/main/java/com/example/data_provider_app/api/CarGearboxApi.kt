package com.example.data_provider_app.api

import com.example.data_provider_app.dto.CarGearboxDto
import retrofit2.Response
import retrofit2.http.GET

interface CarGearboxApi {
    @GET("cargearboxes")
    suspend fun getAllCarGearboxes(): Response<List<CarGearboxDto>>
}

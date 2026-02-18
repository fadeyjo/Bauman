package com.example.data_provider_app.api

import com.example.data_provider_app.dto.EngineTypeDto
import retrofit2.Response
import retrofit2.http.GET

interface EngineTypeApi {
    @GET("enginetypes")
    suspend fun getAllEngineTypes(): Response<List<EngineTypeDto>>
}

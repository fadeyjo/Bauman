package com.example.data_provider_app.api

import com.example.data_provider_app.dto.CarBodyDto
import retrofit2.Response
import retrofit2.http.GET

interface CarBodyApi {
    @GET("carbodies")
    suspend fun getAllCarBodies(): Response<List<CarBodyDto>>
}

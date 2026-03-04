package com.example.data_provider_app.api

import retrofit2.Response
import retrofit2.http.GET

interface CarBrandApi {
    @GET("carbrands")
    suspend fun getAllBrands(): Response<List<String>>
}

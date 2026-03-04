package com.example.data_provider_app.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CarBrandModelApi {
    @GET("carbrandsmodels/{brandName}")
    suspend fun getAllModelsByBrand(@Path("brandName") brandName: String): Response<List<String>>
}
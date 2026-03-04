package com.example.data_provider_app.repository

import com.example.data_provider_app.api.CarBrandModelApi
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult

class CarBrandsModelsRepository(
    private val api: CarBrandModelApi
) {
    suspend fun getAllModelsByBrand(brandName: String): ApiResult<List<String>> {
        return try {
            ApiResponseHandler.handleResponse(api.getAllModelsByBrand(brandName))
        }
        catch (ex: Exception) {
            ApiResult.NetworkError
        }
    }
}
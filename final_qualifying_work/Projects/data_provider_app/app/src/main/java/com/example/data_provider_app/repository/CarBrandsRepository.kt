package com.example.data_provider_app.repository

import com.example.data_provider_app.api.CarBrandApi
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult

class CarBrandsRepository(
    private val api: CarBrandApi
) {
    suspend fun getAllBrands(): ApiResult<List<String>> {
        return try {
            ApiResponseHandler.handleResponse(api.getAllBrands())
        } catch (ex: Exception) {
            ApiResult.NetworkError
        }
    }
}
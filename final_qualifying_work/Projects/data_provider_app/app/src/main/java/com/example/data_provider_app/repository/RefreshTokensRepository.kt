package com.example.data_provider_app.repository

import com.example.data_provider_app.api.RefreshTokenApi
import com.example.data_provider_app.dto.AuthorizedDto
import com.example.data_provider_app.dto.CheckTokenDto
import com.example.data_provider_app.dto.RefreshTokensDto
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult

class RefreshTokensRepository(
    private val api: RefreshTokenApi
) {
    suspend fun refresh(refreshToken: String): ApiResult<AuthorizedDto> {
        val refreshData = RefreshTokensDto(refreshToken)

        return try {
            ApiResponseHandler.handleResponse(api.refresh(refreshData))
        }
        catch (e: Exception) {
            ApiResult.NetworkError
        }
    }

    suspend fun checkToken(refreshToken: String): ApiResult<Unit> {
        val data = CheckTokenDto(refreshToken)

        return try {
            ApiResponseHandler.handleResponse(api.checkToken(data))
        }
        catch (e: Exception) {
            ApiResult.NetworkError
        }
    }
}
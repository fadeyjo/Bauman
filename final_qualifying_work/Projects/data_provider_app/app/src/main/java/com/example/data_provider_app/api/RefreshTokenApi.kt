package com.example.data_provider_app.api

import com.example.data_provider_app.dto.AuthorizedDto
import com.example.data_provider_app.dto.CheckTokenDto
import com.example.data_provider_app.dto.RefreshTokensDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface RefreshTokenApi {
    @POST("refreshtokens/refresh")
    suspend fun refresh(@Body refreshData: RefreshTokensDto): Response<AuthorizedDto>

    @POST("refreshtokens/check_token")
    suspend fun checkToken(@Body data: CheckTokenDto): Response<Unit>
}
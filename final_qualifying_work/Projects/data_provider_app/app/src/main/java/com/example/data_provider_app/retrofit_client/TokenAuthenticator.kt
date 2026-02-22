package com.example.data_provider_app.retrofit_client

import com.example.data_provider_app.api.RefreshTokenApi
import com.example.data_provider_app.dto.RefreshTokensDto
import com.example.data_provider_app.jwt.TokenStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val refreshApi: RefreshTokenApi
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        if (response.priorResponse != null) {
            return null
        }

        val refreshToken = TokenStorage.getRefreshToken()
            ?: return null

        val refreshResponse = runBlocking {
            try {
                refreshApi.refresh(RefreshTokensDto(refreshToken))
            } catch (e: Exception) {
                null
            }
        }

        if (refreshResponse?.isSuccessful == true) {

            val body = refreshResponse.body() ?: return null

            TokenStorage.saveTokens(
                body.accessToken,
                body.refreshToken
            )

            return response.request.newBuilder()
                .header("Authorization", "Bearer ${body.accessToken}")
                .build()
        }

        TokenStorage.clear()
        return null
    }
}

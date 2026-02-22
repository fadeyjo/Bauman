package com.example.data_provider_app.repository

import com.example.data_provider_app.api.AvatarApi
import com.example.data_provider_app.dto.AvatarDto
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult

class AvatarRepository(
    private val api: AvatarApi
) {
    suspend fun newAvatar(): ApiResult<AvatarDto> {
        return try {
            ApiResponseHandler.handleResponse(api.newAvatar())
        }
        catch (e: Exception) {
            ApiResult.NetworkError
        }
    }

    suspend fun getAvatarById(avatarId: UInt) {

    }

    suspend fun getLastAvatar() {

    }
}

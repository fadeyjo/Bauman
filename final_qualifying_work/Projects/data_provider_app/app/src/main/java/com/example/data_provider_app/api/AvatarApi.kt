package com.example.data_provider_app.api

import com.example.data_provider_app.dto.AvatarDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AvatarApi {
    @POST("avatars")
    suspend fun newAvatar(): Response<AvatarDto>

    @GET("avatars/avatar_id/{avatarId}")
    suspend fun getAvatarById(@Path("avatarId") avatarId: UInt)

    @POST("avatars/last")
    suspend fun getLastAvatar()
}

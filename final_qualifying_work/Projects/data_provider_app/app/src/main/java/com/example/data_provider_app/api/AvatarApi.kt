package com.example.data_provider_app.api

import com.example.data_provider_app.dto.AvatarDto
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface AvatarApi {

    @Multipart
    @POST("avatars")
    suspend fun newAvatar(
        @Part file: MultipartBody.Part
    ): Response<AvatarDto>

    @GET("avatars/avatar_id/{avatarId}")
    @Streaming
    suspend fun getAvatarById(
        @Path("avatarId") avatarId: UInt
    ): Response<ResponseBody>

    @GET("avatars/last")
    @Streaming
    suspend fun getLastAvatar(): Response<ResponseBody>
}
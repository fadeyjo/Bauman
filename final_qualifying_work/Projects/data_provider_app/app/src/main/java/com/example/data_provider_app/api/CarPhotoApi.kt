package com.example.data_provider_app.api

import com.example.data_provider_app.dto.CarPhotoDto
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Streaming

interface CarPhotoApi {
    @Multipart
    @POST("carphotos/car_id/{carId}")
    suspend fun newCarPhoto(
        @Part file: MultipartBody.Part,
        @Path("carId") carId: UInt
    ): Response<CarPhotoDto>

    @GET("carphotos/photo_id/{photoId}")
    @Streaming
    suspend fun getPhotoById(
        @Path("photoId") photoId: UInt
    ): Response<ResponseBody>

    @GET("avatars/last/{carId}")
    @Streaming
    suspend fun getLastPhoto(@Path("carId") carId: UInt): Response<ResponseBody>
}
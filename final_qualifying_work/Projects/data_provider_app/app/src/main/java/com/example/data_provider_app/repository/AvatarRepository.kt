package com.example.data_provider_app.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.data_provider_app.api.AvatarApi
import com.example.data_provider_app.dto.AvatarDto
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult
import com.example.data_provider_app.util.ApiResult.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import java.io.File

class AvatarRepository(
    private val api: AvatarApi
) {

    suspend fun newAvatar(file: File): ApiResult<AvatarDto> {
        return try {
            val requestFile =
                file
                    .asRequestBody("image/png".toMediaTypeOrNull())

            val body =
                MultipartBody.Part.createFormData(
                    "file",
                    file.name,
                    requestFile
                )

            ApiResponseHandler.handleResponse(api.newAvatar(body))
        }
        catch (e: Exception) {
            NetworkError
        }
    }

    suspend fun getAvatarById(avatarId: UInt): ApiResult<Bitmap> {
        return try {
            val response = api.getAvatarById(avatarId)

            if (response.isSuccessful) {
                val bytes = response.body()?.bytes()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes!!.size)
                Success(bitmap)
            }
            else {
                val  handleResponse = ApiResponseHandler.handleResponse(response)

                when (handleResponse) {
                    is Error -> Error(handleResponse.code, handleResponse.error)
                    is NetworkError -> NetworkError
                    is Success -> UnknownError
                    is UnknownError -> UnknownError
                    is ValidationError -> ValidationError(handleResponse.code, handleResponse.errors)
                }
            }
        }
        catch (e: Exception) {
            NetworkError
        }
    }

    suspend fun getLastAvatar(): ApiResult<Bitmap> {
        return try {
            val response = api.getLastAvatar()

            if (response.isSuccessful) {
                val bytes = response.body()?.bytes()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes!!.size)
                Success(bitmap)
            }
            else {
                val  handleResponse = ApiResponseHandler.handleResponse(response)

                when (handleResponse) {
                    is Error -> Error(handleResponse.code, handleResponse.error)
                    is NetworkError -> NetworkError
                    is Success -> UnknownError
                    is UnknownError -> UnknownError
                    is ValidationError -> ValidationError(handleResponse.code, handleResponse.errors)
                }
            }
        }
        catch (e: Exception) {
            NetworkError
        }
    }
}
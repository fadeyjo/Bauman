package com.example.data_provider_app.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.data_provider_app.api.CarPhotoApi
import com.example.data_provider_app.dto.CarPhotoDto
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult
import com.example.data_provider_app.util.ApiResult.Error
import com.example.data_provider_app.util.ApiResult.NetworkError
import com.example.data_provider_app.util.ApiResult.Success
import com.example.data_provider_app.util.ApiResult.ValidationError
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class CarPhotosRepository(
    private val api: CarPhotoApi
) {
    suspend fun newCarPhoto(file: File, mimeType: String, carId: UInt): ApiResult<CarPhotoDto> {
        return try {

            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())

            val body = MultipartBody.Part.createFormData(
                "file",
                file.name,
                requestFile
            )

            ApiResponseHandler.handleResponse(api.newCarPhoto(body, carId))
        }
        catch (e: Exception) {
            NetworkError
        }
    }

    suspend fun getPhotoById(photoId: UInt): ApiResult<Bitmap> {
        return try {
            val response = api.getPhotoById(photoId)

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
                    is Success -> ApiResult.UnknownError
                    is ApiResult.UnknownError -> ApiResult.UnknownError
                    is ValidationError -> ValidationError(handleResponse.code, handleResponse.errors)
                }
            }
        }
        catch (e: Exception) {
            NetworkError
        }
    }

    suspend fun getLastPhoto(carId: UInt): ApiResult<Bitmap> {
        return try {
            val response = api.getLastPhoto(carId)

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
                    is Success -> ApiResult.UnknownError
                    is ApiResult.UnknownError -> ApiResult.UnknownError
                    is ValidationError -> ValidationError(handleResponse.code, handleResponse.errors)
                }
            }
        }
        catch (e: Exception) {
            NetworkError
        }
    }
}
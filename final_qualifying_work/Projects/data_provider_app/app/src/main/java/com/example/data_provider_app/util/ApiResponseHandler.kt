package com.example.data_provider_app.util

import com.example.data_provider_app.dto.ErrorDto
import com.example.data_provider_app.dto.ValidationErrorDto
import com.google.gson.Gson
import okio.IOException
import retrofit2.Response

object ApiResponseHandler {
    fun <T> handleResponse(response: Response<T>): ApiResult<T> {
        try {
            if (response.isSuccessful) {
                return ApiResult.Success(response.body())
            }

            val errorBody = response.errorBody()?.string()
            val gson = Gson()

            try {
                val validationErrorRes = gson.fromJson(
                    errorBody,
                    ValidationErrorDto::class.java
                )
                return ApiResult.ValidationError(validationErrorRes.status, validationErrorRes.errors)
            } catch (_: Exception) {}

            try {
                val errorRes = gson.fromJson(
                    errorBody,
                    ErrorDto::class.java
                )
                return ApiResult.Error(errorRes.status, errorRes.title)
            } catch (_: Exception) {}

            return ApiResult.UnknownError
        } catch (e: IOException) {
            return ApiResult.NetworkError
        } catch (e: Exception) {
            return ApiResult.UnknownError
        }
    }
}
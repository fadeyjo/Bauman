package com.example.data_provider_app.util

import com.example.data_provider_app.model.dto.ApiErrorResponse
import com.example.data_provider_app.model.dto.ValidationErrorResponse
import com.google.gson.Gson
import okio.IOException
import retrofit2.Response

object ApiResponseHandler {
    fun <T> handleResponse(response: Response<T>): ApiResult<T> {
        return try {
            if (response.isSuccessful) {
                ApiResult.Success(response.body()!!)
            }
            else {
                val errorBody = response.errorBody()?.string()
                val gson = Gson()

                when (response.code()) {
                    400 -> {
                        try {
                            val validationError = gson.fromJson(
                                errorBody,
                                ValidationErrorResponse::class.java
                            )
                            return if (validationError.errors != null) {
                                ApiResult.BadRequest(
                                    validationError.errors
                                        .values
                                        .firstOrNull()
                                        ?.firstOrNull()
                                )
                            }
                            else {
                                ApiResult.BadRequest(null)
                            }
                        } catch (_: Exception) {}

                        try {
                            val apiError = gson.fromJson(
                                errorBody,
                                ApiErrorResponse::class.java
                            )
                            return ApiResult.BadRequest(apiError.error)
                        } catch (_: Exception) {}

                        ApiResult.BadRequest("Ошибка запроса")
                    }

                    401 -> ApiResult.Unauthorized
                    403 -> ApiResult.Forbidden
                    404 -> ApiResult.NotFound
                    500 -> {
                        try {
                            val internalServerError = gson.fromJson(
                                errorBody,
                                ApiErrorResponse::class.java
                            )
                            return ApiResult.InternalServerError(internalServerError.error)
                        } catch (_: Exception) {}

                        ApiResult.InternalServerError("Ошибка сервера")
                    }
                    else -> ApiResult.UnknownError
                }
            }
        } catch (e: IOException) {
            ApiResult.NetworkError
        } catch (e: Exception) {
            ApiResult.UnknownError
        }
    }
}
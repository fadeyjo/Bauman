package com.example.data_provider_app.util

sealed class ApiResult<out T> {
    data class Success<T>(val data: T?) : ApiResult<T>()
    data class Error(
        val code: UShort,
        val error: String
    ) : ApiResult<Nothing>()

    data class ValidationError(
        val code: UShort,
        val errors: Map<String, List<String>>
    ) : ApiResult<Nothing>()

    object NetworkError : ApiResult<Nothing>()

    object UnknownError : ApiResult<Nothing>()
}
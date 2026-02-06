package com.example.data_provider_app.util

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class BadRequest(val error: String?) : ApiResult<Nothing>()
    data object Unauthorized : ApiResult<Nothing>()
    data object Forbidden : ApiResult<Nothing>()
    data object NotFound : ApiResult<Nothing>()
    data class InternalServerError(val error: String?) : ApiResult<Nothing>()
    data object NetworkError : ApiResult<Nothing>()
    data object UnknownError : ApiResult<Nothing>()
}
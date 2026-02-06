package com.example.data_provider_app.repository

import com.example.data_provider_app.model.dto.CheckPasswordRequest
import com.example.data_provider_app.model.dto.CheckPasswordResponse
import com.example.data_provider_app.model.dto.CreateCarRequest
import com.example.data_provider_app.model.dto.CreateCarResponse
import com.example.data_provider_app.model.dto.PersonIdResponse
import com.example.data_provider_app.model.dto.RegisterPersonRequest
import com.example.data_provider_app.model.entity.Person
import com.example.data_provider_app.remote.ApiService
import com.example.data_provider_app.remote.RetrofitClient
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult

object PersonRepository {
    suspend fun getPersonByEmail(email: String): ApiResult<Person> {
        val response = RetrofitClient.api.getPersonByEmail(email)
        return ApiResponseHandler.handleResponse(response)
    }

    suspend fun checkPassword(data: CheckPasswordRequest): ApiResult<CheckPasswordResponse> {
        val response = RetrofitClient.api.checkPassword(data)
        return ApiResponseHandler.handleResponse(response)
    }

    suspend fun registerPerson(person: RegisterPersonRequest): ApiResult<PersonIdResponse> {
        val response = RetrofitClient.api.registerPerson(person)
        return ApiResponseHandler.handleResponse(response)
    }
}
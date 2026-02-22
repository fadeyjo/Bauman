package com.example.data_provider_app.api

import com.example.data_provider_app.dto.AuthorizedDto
import com.example.data_provider_app.dto.LoginDto
import com.example.data_provider_app.dto.CreatePersonDto
import com.example.data_provider_app.dto.PersonDto
import com.example.data_provider_app.dto.UpdatePersonInfoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface PersonApi {
    @GET("persons/email")
    suspend fun getPersonByEmail(): Response<PersonDto>

    @POST("persons/login")
    suspend fun checkPassword(@Body loginData: LoginDto): Response<AuthorizedDto>

    @POST("persons")
    suspend fun createPerson(@Body personData: CreatePersonDto): Response<PersonDto>

    @PUT("persons")
    suspend fun updatePersonInfo(@Body updatedPersonInfo: UpdatePersonInfoDto): Response<Unit>

    @PUT("persons/logout")
    suspend fun logout(): Response<Unit>
}

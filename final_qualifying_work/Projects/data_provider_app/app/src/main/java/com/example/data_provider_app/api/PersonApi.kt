package com.example.data_provider_app.api

import com.example.data_provider_app.dto.CheckPasswordDto
import com.example.data_provider_app.dto.CreatePersonDto
import com.example.data_provider_app.dto.PersonDto
import com.example.data_provider_app.dto.UpdatePersonInfoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PersonApi {
    @GET("persons/email/{email}")
    suspend fun getPersonByEmail(@Path("email") email: String): Response<PersonDto>

    @POST("persons/check_password")
    suspend fun checkPassword(@Body checkPasswordData: CheckPasswordDto): Response<PersonDto>

    @POST("persons")
    suspend fun createPerson(@Body personData: CreatePersonDto): Response<PersonDto>

    @PUT("persons")
    suspend fun updatePersonInfo(@Body updatedPersonInfo: UpdatePersonInfoDto): Response<Unit>
}

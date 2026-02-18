package com.example.data_provider_app.repository

import com.example.data_provider_app.api.PersonApi
import com.example.data_provider_app.dto.CheckPasswordDto
import com.example.data_provider_app.dto.CreatePersonDto
import com.example.data_provider_app.dto.PersonDto
import com.example.data_provider_app.dto.UpdatePersonInfoDto
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult
import java.time.LocalDate

class PersonsRepository(
    private val api: PersonApi
) {
    suspend fun getPersonByEmail(email: String): ApiResult<PersonDto> {
        return ApiResponseHandler.handleResponse(api.getPersonByEmail(email))
    }

    suspend fun checkPassword(email: String, password: String): ApiResult<PersonDto> {
        val checkPassword = CheckPasswordDto(email, password)

        return ApiResponseHandler.handleResponse(api.checkPassword(checkPassword))
    }

    suspend fun createPerson(
        email: String, phone: String,
        lastName: String, firstName: String,
        patronymic: String?, birth: LocalDate,
        password: String, driveLisense: String?,
        rightLevel: UByte
    ): ApiResult<PersonDto> {
        val person = CreatePersonDto(
            email, phone,
            lastName, firstName,
            patronymic, birth,
            password, driveLisense,
            rightLevel
        )

        return ApiResponseHandler.handleResponse(api.createPerson(person))
    }

    suspend fun updatePersonInfo(
        personId: UInt, email: String,
        phone: String, lastName: String,
        firstName: String, patronymic: String?,
        birth: LocalDate, driveLisense: String?,
    ): ApiResult<Unit> {
        val updatedPerson = UpdatePersonInfoDto(
            personId, email, phone,
            lastName, firstName,
            patronymic, birth,
            driveLisense,
        )

        return ApiResponseHandler.handleResponse(api.updatePersonInfo(updatedPerson))
    }
}
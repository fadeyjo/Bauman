package com.example.data_provider_app.repository

import com.example.data_provider_app.api.PersonApi
import com.example.data_provider_app.dto.AuthorizedDto
import com.example.data_provider_app.dto.LoginDto
import com.example.data_provider_app.dto.CreatePersonDto
import com.example.data_provider_app.dto.PersonDto
import com.example.data_provider_app.dto.UpdatePersonInfoDto
import com.example.data_provider_app.util.ApiResponseHandler
import com.example.data_provider_app.util.ApiResult
import java.time.LocalDate

class PersonsRepository(
    private val api: PersonApi
) {
    suspend fun getPersonByEmail(): ApiResult<PersonDto> {
        return try {
            ApiResponseHandler.handleResponse(api.getPersonByEmail())
        }
        catch (e: Exception) {
            ApiResult.NetworkError
        }
    }

    suspend fun login(email: String, password: String): ApiResult<AuthorizedDto> {
        val loginData = LoginDto(email, password)

        return try {
            ApiResponseHandler.handleResponse(api.checkPassword(loginData))
        }
        catch (e: Exception) {
            ApiResult.NetworkError
        }
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

        return try {
            ApiResponseHandler.handleResponse(api.createPerson(person))
        }
        catch (e: Exception) {
            ApiResult.NetworkError
        }
    }

    suspend fun updatePersonInfo(
        personId: UInt, email: String,
        phone: String, lastName: String,
        firstName: String, patronymic: String?,
        birth: LocalDate, driveLicense: String?,
    ): ApiResult<Unit> {
        val updatedPerson = UpdatePersonInfoDto(
            email, phone,
            lastName, firstName,
            patronymic, birth,
            driveLicense,
        )

        return try {
            ApiResponseHandler.handleResponse(api.updatePersonInfo(updatedPerson))
        }
        catch (e: Exception) {
            ApiResult.NetworkError
        }
    }

    suspend fun logout(): ApiResult<Unit> {
        return try {
            ApiResponseHandler.handleResponse(api.logout())
        }
        catch (e: Exception) {
            ApiResult.NetworkError
        }
    }
}
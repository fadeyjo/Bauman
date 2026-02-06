package com.example.data_provider_app.remote

import com.example.data_provider_app.model.dto.CarResponse
import com.example.data_provider_app.model.dto.CheckPasswordRequest
import com.example.data_provider_app.model.dto.CheckPasswordResponse
import com.example.data_provider_app.model.dto.CreateCarRequest
import com.example.data_provider_app.model.dto.CreateCarResponse
import com.example.data_provider_app.model.dto.CreateGPSDataRequest
import com.example.data_provider_app.model.dto.CreateTelemetryDataRequest
import com.example.data_provider_app.model.dto.EndTripRequest
import com.example.data_provider_app.model.dto.PersonIdResponse
import com.example.data_provider_app.model.dto.RecIdResponse
import com.example.data_provider_app.model.dto.RegisterPersonRequest
import com.example.data_provider_app.model.dto.StartTripRequest
import com.example.data_provider_app.model.dto.TripIdResponse
import com.example.data_provider_app.model.entity.Person
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("persons/email/{email}")
    suspend fun getPersonByEmail(@Path("email") email: String): Response<Person>

    @POST("persons/check_password")
    suspend fun checkPassword(@Body data: CheckPasswordRequest): Response<CheckPasswordResponse>

    @POST("persons")
    suspend fun registerPerson(@Body person: RegisterPersonRequest): Response<PersonIdResponse>

    @POST("cars")
    suspend fun addCar(@Body car: CreateCarRequest): Response<CreateCarResponse>

    @GET("cars/person_id/{personId}")
    suspend fun getCarsByPersonId(@Path("personId") personId: UInt): Response<List<CarResponse>>

    @POST("trips")
    suspend fun startTrip(@Body tripData: StartTripRequest): Response<TripIdResponse>

    @PUT("trips/end/{tripId}")
    suspend fun endTrip(@Body endData: EndTripRequest, @Path("tripId") tripId: ULong): Response<TripIdResponse>

    @POST("gpsdata")
    suspend fun createGPSData(@Body gpsData: CreateGPSDataRequest): Response<RecIdResponse>

    @POST("telemetrydata")
    suspend fun createTelemtryData(@Body telemetryData: CreateTelemetryDataRequest): Response<RecIdResponse>
}
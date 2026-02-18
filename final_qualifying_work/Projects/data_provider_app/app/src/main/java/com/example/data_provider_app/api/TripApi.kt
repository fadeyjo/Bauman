package com.example.data_provider_app.api

import com.example.data_provider_app.dto.CarBodyDto
import com.example.data_provider_app.dto.CarDriveDto
import com.example.data_provider_app.dto.CarDto
import com.example.data_provider_app.dto.CarGearboxDto
import com.example.data_provider_app.dto.CheckPasswordDto
import com.example.data_provider_app.dto.CreateCarDto
import com.example.data_provider_app.dto.CreateGPSDataDto
import com.example.data_provider_app.dto.CreatePersonDto
import com.example.data_provider_app.dto.CreateTelemetryDataDto
import com.example.data_provider_app.dto.EngineTypeDto
import com.example.data_provider_app.dto.FuelTypeDto
import com.example.data_provider_app.dto.GPSDataDto
import com.example.data_provider_app.dto.PersonDto
import com.example.data_provider_app.dto.StartTripDto
import com.example.data_provider_app.dto.TelemetryDataDto
import com.example.data_provider_app.dto.TripDto
import com.example.data_provider_app.dto.UpdateCarInfoDto
import com.example.data_provider_app.dto.UpdatePersonInfoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {


















    @POST("trips")
    suspend fun startTrip(@Body tripData: StartTripDto): Response<TripDto>

    @GET("trips/trip_id/{tripId}")
    suspend fun getTripById(@Path("tripId") tripId: ULong): Response<TripDto>

    @PUT("trips/end/{tripId}")
    suspend fun endTrip(@Path("tripId") tripId: ULong): Response<Unit>
}

package com.example.data_provider_app.api

import com.example.data_provider_app.dto.CarDto
import com.example.data_provider_app.dto.CreateCarDto
import com.example.data_provider_app.dto.UpdateCarInfoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CarApi {
    @GET("cars/vin/{vin}")
    suspend fun getCarByVINNumber(@Path("vin") VINNumber: String): Response<List<CarDto>>

    @POST("cars")
    suspend fun addCar(@Body car: CreateCarDto): Response<CarDto>

    @GET("cars/person_id/{personId}")
    suspend fun getCarsByPersonId(@Path("personId") personId: UInt): Response<List<CarDto>>

    @PUT("cars")
    suspend fun updateCarInfo(@Body updatedCarData: UpdateCarInfoDto): Response<Unit>
}

package com.example.data_provider_app.api

import com.example.data_provider_app.dto.StartTripDto
import com.example.data_provider_app.dto.TripDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TripApi {
    @POST("trips")
    suspend fun startTrip(@Body tripData: StartTripDto): Response<TripDto>

    @GET("trips/trip_id/{tripId}")
    suspend fun getTripById(@Path("tripId") tripId: ULong): Response<TripDto>

    @PUT("trips/end/{tripId}")
    suspend fun endTrip(@Path("tripId") tripId: ULong): Response<Unit>
}

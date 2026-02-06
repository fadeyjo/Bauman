package com.example.data_provider_app.model.dto

data class StartTripRequest(
    val startDatetime: String,
    val MACAddress: String,
    val carId: UInt
)

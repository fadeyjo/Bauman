package com.example.data_provider_app.model.dto

data class CreateTelemetryDataRequest(
    val recDatetime: String,
    val serviceId: UByte,
    val PID: UShort,
    val ECUId: String,
    val responseDlc: UByte,
    val response: String?,
    val tripId: ULong
)

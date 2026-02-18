package com.example.data_provider_app.dto

import java.time.LocalDateTime

data class CreateTelemetryDataDto(
    val recDatetime: LocalDateTime,
    val serviceId: UByte,
    val PID: UShort,
    val ECUId: ByteArray,
    val responseDlc: UByte,
    val response: ByteArray?,
    val tripId: ULong
)

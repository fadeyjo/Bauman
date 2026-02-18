package com.example.data_provider_app.dto

import java.time.LocalDateTime

data class TelemetryDataDto(
    val recId: ULong,
    val recDatetime: LocalDateTime,
    val serviceId: UByte,
    val PID: UShort,
    val ECUId: ByteArray,
    val responseDLC: UByte,
    val response: ByteArray?,
    val tripId: ULong
)

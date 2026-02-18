package com.example.data_provider_app.dto

import java.time.LocalDateTime

data class TripDto(
    val tripId: ULong,
    val startDatetime: LocalDateTime,
    val MACAddress: String,
    val VINNumber: String,
    val endDatetime: LocalDateTime?
)

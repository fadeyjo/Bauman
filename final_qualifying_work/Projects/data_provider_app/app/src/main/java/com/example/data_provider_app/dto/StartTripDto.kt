package com.example.data_provider_app.dto

import java.time.LocalDateTime

data class StartTripDto(
    val startDatetime: LocalDateTime,
    val MACAddress: String,
    val carId: UInt
)
